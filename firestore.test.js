const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const ADMIN_UID = "admin_789";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read users", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Unauthenticated user: cannot read privateProfiles", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("privateProfiles").doc(ALICE_UID).get());
});

test("Cross-user isolation: Bob cannot read Alice's privateProfile", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("privateProfiles").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "Alice Gamer",
      age: 21,
      fullUid: "FF_12345678",
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("privateProfiles").doc(ALICE_UID).get());
});

test("Owner access: Alice can read her own privateProfile", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("privateProfiles").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "Alice Gamer",
      age: 21,
      fullUid: "FF_12345678",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("privateProfiles").doc(ALICE_UID).get());
});

test("Public profiles: signed-in users can read", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("publicProfiles").doc(ALICE_UID).set({
      userId: ALICE_UID,
      ign: "Alice_FF",
      preferredMode: "BR Ranked",
      experience: "Advanced",
      playingStyle: "Rusher",
      playingSchedule: "Night",
      language: "Bengali",
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertSucceeds(bobDb.collection("publicProfiles").doc(ALICE_UID).get());
});

test("Admin access: hafezabdurrahman22@gmail.com can read Alice's privateProfile", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("privateProfiles").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "Alice Gamer",
      age: 21,
      fullUid: "FF_12345678",
      createdAt: new Date(),
    });
  });

  const adminDb = testEnv.authenticatedContext(ADMIN_UID, { email: "hafezabdurrahman22@gmail.com" }).firestore();
  await assertSucceeds(adminDb.collection("privateProfiles").doc(ALICE_UID).get());
});
