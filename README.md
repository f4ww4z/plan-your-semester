[![Build Status](https://travis-ci.com/f4ww4z/plan-your-semester.svg?token=6sGe8XeJSLjUzd5ZEHK5&branch=master)](https://travis-ci.com/f4ww4z/plan-your-semester)

<a href='https://play.google.com/store/apps/details?id=com.jagoancoding.planyoursemester&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' width="360"/></a>

# Plan Your Semester

An Android app to keep track of your everyday plans.

![Screenshots](https://i.imgur.com/PY6oXER.png)


## Want to Contribute?

FInd an *unassigned*, *ready-for-work* [open issues](https://github.com/f4ww4z/plan-your-semester/issues) and assign yourself to one to get started. For first-time contributors, feel free to check out [**good first issues**](https://github.com/f4ww4z/plan-your-semester/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+first+issue%22)

#### Setting up the Development Environment

1. Fork this repository
2. Copy the **clone** URL.
3. Open [Android Studio 3+](https://developer.android.com/studio/)
4. Choose 'check out from version control' and paste the forked repo URL you copied earlier. Choose a root *directory* that this project will reside in.
5. Wait for gradle sync to finish.

#### Translating

1. Open this project in Android Studio using the instructions above.
2. Go to `app/res/values/`
3. Right click on `strings.xml` and choose **Open Translation Editor**
4. Choose the *Globe* button (named *Add Locale*) and choose a language
4. Begin translating in the newly created language column

## Notes

- To run the `MainViewModelTest`, put this in the project's `gradle.properties`: `android.enableUnitTestBinaryResources=true`