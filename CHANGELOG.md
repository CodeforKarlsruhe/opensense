# Changelog
An app with widgets for the great [openSenseMap](https://opensensemap.org) project.

## 0.6.0 (2011-06-13)
Thanks to [Tobias Preuss](https://github.com/johnjohndoe) for contributing the below changes.
- Update of several dependencies
- Build: Use gradle-versions-plugin to detect version updates
- Build: Debug build variant with different package name to enable parallel install

## 0.4.1 (2019-04-10)
#### Fixed
- Update mapbox dependency to fix a crash with the map view

## 0.4.0 (2019-04-10)
Update build script, API calls and migrate to Gradle Kotlin DSL

## 0.3.0 (2018-01-29)
Expand the features

#### Added
- The `Plot Widget`, which shows a plot of one sensor value


## 0.2.0 (2017-10-08)
Improve widget design and configuration

#### Added
- Two additional buttons for the widget. They make it possible to refresh and change the configuration

#### Changed
- The senseBox is now selected in the configuration due a map and not by id 


## 0.1.0 (2017-10-02)
Initial release with an app widget.

#### Added
- The `One Value Widget`, which shows one sensor value of a senseBox
- A configuration screen to select the senseBox and a sensor for the widget

