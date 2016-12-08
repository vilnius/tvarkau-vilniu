# Tvarkau Vilnių

[![Build Status](https://travis-ci.org/vilnius/tvarkau-vilniu.svg?branch=master)](https://travis-ci.org/vilnius/tvarkau-vilniu)

[![Build Status](https://www.bitrise.io/app/5090fdc91fb4c2df.svg?token=FCegO6CX1S_BY3-u4FX5LQ&branch=master)](https://www.bitrise.io/artifact/39663/p/ea386dc8de7f2989f7869ae3c94a2e51) - download latest build from [Bitrise](https://www.bitrise.io/artifact/39663/p/ea386dc8de7f2989f7869ae3c94a2e51).

## Intro/įžanga
### Lietuviškai
Repozitorija naujos Android programėlės programiniam kodui.
Visa tolimesnė vidinė komunikacija vyksta angliškai, nes komandoje yra ir užsieniečių.

### English
Repo for new Android app source-code.
Since there are foreigners in the team, all other further communication will be in english.

## IM
For instant messaging with team join [Tvarkau-Vilnių channel on Slack](https://codeforvilnius.slack.com/messages/tvarkau-vilniu/).

## Tasks, issues
Some tasks are in [Trello board](https://trello.com/b/PE4yjVzw/tvarkau-vilniu).

Post new issues, suggestions, questions or ideas here in [Github's issues section](https://github.com/vilnius/tvarkau-vilniu/issues).

## Tests
To run tests from command line run `./gradlew testDebug`

To run tests from Android Studio select test class or package, right click and select `Run Tests`

## Versioning
Fluid versioning is used for this project.

The number in the first position (**1**.2.3.4) indicates a **major release**.

The number in the second position (1.**2**.3.4) indicates a **minor release**.

The number in the third position (1.2.**3**.4) indicates a **bug fix release**.

The number in the fourth position (1.2.3.**4**) indicates a **special micro release**.

Version numbers should be changed in `app/build.gradle` file.

## Publishing

After merging pull request with master branch app will be automatically published to Google play Beta track and tag with version name will appear in GitHub.


### Note for Mac/Linux users
follow small guide at http://robolectric.org/getting-started/ section `Note for Linux and Mac Users`
