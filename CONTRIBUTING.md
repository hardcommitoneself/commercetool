These are the contribution guidelines for the SPHERE.IO JVM SDK 1.x and Sunrise Java shop template.

Contributions are welcome!

## Contribution process for all committers

### Typos 

If you have push access to the repository you can fix them directly otherwise just make a pull request.

### Code

1. on bigger effort changes: open an issue and ask if you can/should/need to help
1. fork the repository
1. produce production code and unit tests
1. make a pull request
1. mention and/or assign @lauraluiz and @schleichardt, who will review and optionally merge

## Requirements for a pull request

We want to have a clear and tested code base.

* change as few lines as necessary, do not mix up concerns
* never reformat code, we want in diffs only real changes, except it is a pure refactoring pull request
* use code formatting and concepts like in the rest of the application
* your committed code should not emit warnings such as unchecked generics
* you need to use tests to prove that your code works, JUnit tests are sufficient
* production code should be in Java
* your code is from you and not copied from third party sources
* use [good commit messages](http://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html)
* unit tests have to be passed in Travis CI
    * integration tests will fail since we use encrypted credentials which are not available in forks for security reasons
* the SDK should be backwards compatible when released as 1.0.0
