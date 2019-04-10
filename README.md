# Open Sense
An app with widgets for the great [openSenseMap](https://opensensemap.org) project.
It's still work in progress and in an early stage of development.

The current version `0.4.0` is available in the [Play Store](https://play.google.com/store/apps/details?id=de.codefor.karlsruhe.opensense).


## Features
- `One Value Widget`, which shows one sensor value of a senseBox
- `Plot Widget`, which shows a plot of one sensor value


## Development
The app is work in progress and in an early development stage.
Any suggestions, feature requests, bug reports or pull requests are very much appreciated.


Open Sense uses the [Gitflow](https://www.atlassian.com/git/tutorials/comparing-workflows#gitflow-workflow) workflow:
- All pull requests should be branched from develop
- The pull request is merged into develop
- The develop branch is merged into master for the next release

To work correctly, Open Sense requires an API token from [mapbox](https://www.mapbox.com/). Setup steps:
- Register for the free mapbox account
- Go to [API access token](https://www.mapbox.com/studio/account/tokens/) and get your API access token
- Define the `mapboxApiToken` variable with the acquired token in the gradle user home `gradle.properties`.
For Linux and Mac this is usually `~/.gradle/gradle.properties`

Example of the `gradle.properties`:
```
mapboxApiToken="API TOKEN"
```


## License
Copyright (c) Open Knowledge Lab Karlsruhe
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.