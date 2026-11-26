package cat.xlagunas.conference.ui

import android.Manifest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PermissionsOutcomeTest {
    private val both = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

    @Test
    fun givenAllRequestedGranted_whenPermissionsOutcome_thenStart() {
        val outcome =
            permissionsOutcome(
                requested = both,
                result = mapOf(Manifest.permission.CAMERA to true, Manifest.permission.RECORD_AUDIO to true),
                hasPendingRequest = true,
                shouldShowRationale = { true },
            )

        assertThat(outcome).isEqualTo(PermissionsOutcome.Start)
    }

    @Test
    fun givenPartialGrantWithRationale_whenPermissionsOutcome_thenShowRetry() {
        val outcome =
            permissionsOutcome(
                requested = both,
                result = mapOf(Manifest.permission.CAMERA to true, Manifest.permission.RECORD_AUDIO to false),
                hasPendingRequest = true,
                shouldShowRationale = { it == Manifest.permission.RECORD_AUDIO },
            )

        assertThat(outcome).isEqualTo(PermissionsOutcome.ShowRetry)
    }

    @Test
    fun givenDeniedWithoutRationale_whenPermissionsOutcome_thenShowSettings() {
        val outcome =
            permissionsOutcome(
                requested = arrayOf(Manifest.permission.CAMERA),
                result = mapOf(Manifest.permission.CAMERA to false),
                hasPendingRequest = true,
                shouldShowRationale = { false },
            )

        assertThat(outcome).isEqualTo(PermissionsOutcome.ShowSettings)
    }

    @Test
    fun givenRationaleOnlyForGrantedPermission_whenPermissionsOutcome_thenShowSettings() {
        val outcome =
            permissionsOutcome(
                requested = both,
                result = mapOf(Manifest.permission.CAMERA to true, Manifest.permission.RECORD_AUDIO to false),
                hasPendingRequest = true,
                shouldShowRationale = { it == Manifest.permission.CAMERA },
            )

        assertThat(outcome).isEqualTo(PermissionsOutcome.ShowSettings)
    }

    @Test
    fun givenRequestedPermissionMissingFromResult_whenPermissionsOutcome_thenTreatedAsDenied() {
        val outcome =
            permissionsOutcome(
                requested = both,
                result = mapOf(Manifest.permission.CAMERA to true),
                hasPendingRequest = true,
                shouldShowRationale = { true },
            )

        assertThat(outcome).isEqualTo(PermissionsOutcome.ShowRetry)
    }

    @Test
    fun givenEmptyResultWithPendingRequest_whenPermissionsOutcome_thenShowRetry() {
        val outcome =
            permissionsOutcome(
                requested = both,
                result = emptyMap(),
                hasPendingRequest = true,
                shouldShowRationale = { false },
            )

        assertThat(outcome).isEqualTo(PermissionsOutcome.ShowRetry)
    }

    @Test
    fun givenEmptyResultWithoutPendingRequest_whenPermissionsOutcome_thenIgnore() {
        val outcome =
            permissionsOutcome(
                requested = emptyArray(),
                result = emptyMap(),
                hasPendingRequest = false,
                shouldShowRationale = { false },
            )

        assertThat(outcome).isEqualTo(PermissionsOutcome.Ignore)
    }

    @Test
    fun givenGrantedResultWithoutPendingRequest_whenPermissionsOutcome_thenIgnore() {
        val outcome =
            permissionsOutcome(
                requested = emptyArray(),
                result = mapOf(Manifest.permission.CAMERA to true, Manifest.permission.RECORD_AUDIO to true),
                hasPendingRequest = false,
                shouldShowRationale = { false },
            )

        assertThat(outcome).isEqualTo(PermissionsOutcome.Ignore)
    }
}
