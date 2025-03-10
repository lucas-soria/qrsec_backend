# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.0] - 2025-03-03 ([#13](https://github.com/lucas-soria/qrsec_backend/pull/13))
### Removed
- Removed password from User entity.
- Removed password encoder service.

### Added
- Guest validation before to showing public invite info.
  - SimplifiedGuestDTO.
- Auth google endpoint and service.
  - AuthResponseDTO.

### Changed
- Timestamp handling on the validateInvite endpoint (/invites/validate/{id}).
- Invites are created with status "enabled".
- First user is created with role admin and is enabled by default.

## [0.0.5] - 2025-01-22 ([#11](https://github.com/lucas-soria/qrsec_backend/pull/11))
### Removed
- /admin/ endpoints.

### Changed
- Modified other endpoints to use Roles as authority levels to retrieve information.

### Fixed
- CORS policies had to be lists.

## [0.0.4] - 2025-01-21 ([#9](https://github.com/lucas-soria/qrsec_backend/pull/9))
### Added
- Invite validation algorithm and endpoint.
- Invite "action" endpoint.

### Fixed
- Authorization on certain invite's endpoints.

## [0.0.3] - 2025-01-19 ([#7](https://github.com/lucas-soria/qrsec_backend/pull/7))
### Added
- CORS policies as environment variables.

### Removed
- Daemon from gradle build

## [0.0.2] - 2024-02-05 ([#5](https://github.com/soria-lucas/qrsec_backend/pull/5))
### Added
- A complete CRUD that will allow me to build the app on top. 
  - Added Documentation.
  - Added user role validation using Headers (this will be changed once OAuth is added).

## [0.0.1] - 2024-01-26 ([#3](https://github.com/soria-lucas/qrsec_backend/pull/3))
### Refactored
- Migrated dependency manager from maven to gradle. This one has a cleaner and simpler to read config (build.gradle).

## [0.0.0] - 2023-12-25 ([#1](https://github.com/soria-lucas/qrsec_backend/pull/1))
### Added
- Project's last status.
- CHANGELOG file.

## [Released]
