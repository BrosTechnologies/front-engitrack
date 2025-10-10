// Ruta: features/onboarding/domain/model/OnboardingPage.kt
package pe.edu.upc.engitrack.features.onboarding.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import pe.edu.upc.engitrack.R

sealed class OnboardingPage(
    @DrawableRes
    val image: Int,
    @StringRes
    val title: Int,
    @StringRes
    val description: Int
) {
    object First : OnboardingPage(
        image = R.drawable.img_project_tracking,
        title = R.string.onboarding_title_1,
        description = R.string.onboarding_description_1
    )

    object Second : OnboardingPage(
        image = R.drawable.img_team_collaboration,
        title = R.string.onboarding_title_2,
        description = R.string.onboarding_description_2
    )

    object Third : OnboardingPage(
        image = R.drawable.img_real_time_notifications,
        title = R.string.onboarding_title_3,
        description = R.string.onboarding_description_3
    )
}