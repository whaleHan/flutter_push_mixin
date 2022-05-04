#import "FlutterPushMixinPlugin.h"
#if __has_include(<flutter_push_mixin/flutter_push_mixin-Swift.h>)
#import <flutter_push_mixin/flutter_push_mixin-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_push_mixin-Swift.h"
#endif

@implementation FlutterPushMixinPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterPushMixinPlugin registerWithRegistrar:registrar];
}
@end
