#!/bin/sh
# Copies MobileVLCKit (libVLC for iOS, a dynamic framework) into the app and signs it.
# Run from the Xcode build phase after the Kotlin framework is built.
set -e
XCF="$SRCROOT/Frameworks/MobileVLCKit.xcframework"
case "$PLATFORM_NAME" in
  iphonesimulator) SLICE="ios-arm64_i386_x86_64-simulator" ;;
  *) SLICE="ios-arm64_armv7_armv7s" ;;
esac
DEST="$TARGET_BUILD_DIR/$FRAMEWORKS_FOLDER_PATH"
mkdir -p "$DEST"
rsync -a --delete "$XCF/$SLICE/MobileVLCKit.framework" "$DEST/"
# Keep only the architectures being built, so the app ships no unused slices.
BIN="$DEST/MobileVLCKit.framework/MobileVLCKit"
for arch in $(lipo -archs "$BIN"); do
  case " $ARCHS " in *" $arch "*) ;; *) lipo -remove "$arch" "$BIN" -output "$BIN" ;; esac
done
codesign --force --sign "${EXPANDED_CODE_SIGN_IDENTITY:--}" --preserve-metadata=identifier,entitlements "$DEST/MobileVLCKit.framework"
