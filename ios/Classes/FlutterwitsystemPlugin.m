#import "FlutterwitsystemPlugin.h"
#if __has_include(<flutterwitsystem/flutterwitsystem-Swift.h>)
#import <flutterwitsystem/flutterwitsystem-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutterwitsystem-Swift.h"
#endif

@implementation FlutterwitsystemPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterwitsystemPlugin registerWithRegistrar:registrar];
}
@end
