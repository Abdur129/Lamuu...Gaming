package com.example

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.PrivateProfileData
import com.example.data.model.PublicProfileData
import com.example.data.repository.AppRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class DuoConnectRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun saveAndGetPrivateProfile_authenticatedUser_success() = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repository = AppRepository(firestore)

    val profile = PrivateProfileData(
      userId = uid,
      fullName = "Alice In Game",
      age = 22,
      fullUid = "FF_99887766",
      phone = "01700000000"
    )

    withTimeout(TIMEOUT_MS) {
      repository.savePrivateProfile(profile)
      val retrieved = repository.getPrivateProfile(uid)
      assertNotNull(retrieved)
      assertEquals("Alice In Game", retrieved?.fullName)
      assertEquals("FF_99887766", retrieved?.fullUid)
    }
  }

  @Test
  fun getPrivateProfile_crossUserAccess_failsOrReturnsNull() = runBlocking {
    val aliceUid = signInTestUser(ALICE_CROSS_EMAIL)
    val aliceRepo = AppRepository(firestore)

    withTimeout(TIMEOUT_MS) {
      aliceRepo.savePrivateProfile(
        PrivateProfileData(
          userId = aliceUid,
          fullName = "Alice Private",
          age = 20,
          fullUid = "FF_11223344"
        )
      )
    }

    // Now sign in as Bob
    signInTestUser(BOB_EMAIL)
    val bobRepo = AppRepository(firestore)

    withTimeout(TIMEOUT_MS) {
      val result = bobRepo.getPrivateProfile(aliceUid)
      // Because rules deny access, Firestore SDK either catches or returns null
      assertTrue(result == null)
    }
  }

  @Test
  fun savePublicProfile_authenticatedUser_success() = runBlocking {
    val uid = signInTestUser(ALICE_PUB_EMAIL)
    val repository = AppRepository(firestore)

    val pubProfile = PublicProfileData(
      userId = uid,
      ign = "Alice_Queen",
      level = 70,
      preferredMode = "BR Ranked",
      experience = "Advanced",
      playingStyle = "Rusher",
      playingSchedule = "Night",
      language = "Bengali",
      bio = "Looking for active sniper partner"
    )

    withTimeout(TIMEOUT_MS) {
      repository.savePublicProfile(pubProfile)
      val retrieved = repository.getPublicProfile(uid)
      assertNotNull(retrieved)
      assertEquals("Alice_Queen", retrieved?.ign)
      assertEquals("Rusher", retrieved?.playingStyle)
    }
  }

  private companion object {
    const val ALICE_EMAIL = "alice_save@test.com"
    const val ALICE_CROSS_EMAIL = "alice_cross@test.com"
    const val ALICE_PUB_EMAIL = "alice_pub@test.com"
    const val BOB_EMAIL = "bob@test.com"
    const val TIMEOUT_MS = 5000L
  }
}
