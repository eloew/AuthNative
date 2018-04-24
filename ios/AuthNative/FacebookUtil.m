
#import <UIKit/UIKit.h>
#import "FacebookUtil.h"
#import "FBSDKLoginManager.h"
#import "FBSDKLoginManagerLoginResult.h"
#import "FBSDKCoreKit/FBSDKGraphRequest.h"

@implementation FacebookUtil

NSString * const LOGIN = @"onLogin";
NSString * const LOGOUT = @"onLogout";
NSString * const EVENT_KEY = @"eventName";
NSString * const MESSAGE_KEY = @"message";
NSString * const CANCEL_KEY = @"isCancelled";
NSString * const PROFILE_KEY = @"profile";


RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(login:(RCTResponseSenderBlock)callback) {
  FBSDKLoginManager *login = [[FBSDKLoginManager alloc] init];
  [login
   logInWithReadPermissions: @[@"public_profile"]
   fromViewController:nil
   handler:^(FBSDKLoginManagerLoginResult *result, NSError *error) {
     if (error) {
       NSLog(@"Process error");
       NSDictionary *dict = @{ EVENT_KEY : LOGIN, MESSAGE_KEY: @"error"};
       callback(@[[NSNull null], dict]);
     } else if (result.isCancelled) {
       NSLog(@"Cancelled");
       NSDictionary *dict = @{ EVENT_KEY : LOGIN, CANCEL_KEY: @true};
       callback(@[[NSNull null], dict]);
     } else {
       NSLog(@"Logged in");
       NSDictionary *parameters = @{@"fields":@"name,email"};
       [[[FBSDKGraphRequest alloc] initWithGraphPath:@"me" parameters:parameters]
        startWithCompletionHandler:^(FBSDKGraphRequestConnection *connection, id result, NSError *error)
        {
          NSLog(@"fetched user:%@", result);
          NSData * jsonData = [NSJSONSerialization dataWithJSONObject:result options:0 error:nil];
          NSString * jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
          NSDictionary *dict = @{ EVENT_KEY : LOGIN, PROFILE_KEY: jsonString};
          callback(@[dict]);
        }];
     }
   }];
}

RCT_EXPORT_METHOD(logout:(RCTResponseSenderBlock)callback) {
  FBSDKLoginManager *loginManager = [[FBSDKLoginManager alloc] init];
  [loginManager logOut];
  
  NSDictionary *dict = @{ EVENT_KEY : LOGOUT};
  callback(@[[NSNull null], dict]);
}

@end

