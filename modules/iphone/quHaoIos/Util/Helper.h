//
//  Helper.h
//  quHaoApp
//
//  Created by sam on 13-8-10.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"
#import "ReachabilityNew.h"
#import "AESCrypt.h"
#import "ASIHTTPRequest.h"
#import "UserInfo.h"
#import "SBJsonParser.h"
#import "ApiError.h"
#import "GCDiscreetNotificationView.h"
#import "ASIFormDataRequest.h"
@interface Helper : NSObject

//是否已经登录
@property BOOL isLogin;
@property (strong, nonatomic) UIViewController * viewBeforeLogin;
@property (copy, nonatomic) NSString * viewNameBeforeLogin;

//提示框
+ (void)showHUD:(NSString *)text andView:(UIView *)view andHUD:(MBProgressHUD *)hud;
+ (void)showHUD2:(NSString *)text andView:(UIView *)view andSize:(float)width;
+ (void)showHUD3:(NSString *)text andView:(UIView *)view;
//修改cell小箭头图片
+ (void)arrowStyle:(UITableViewCell *)cell;
//自定义图片长宽
+(UIImage *)reSizeImage:(NSString *)name toSize:(CGSize)reSize;
//等比率缩放
+(UIImage *)scaleImage:(UIImage *)image toScale:(float)scaleSize;
+(UIImage *)scaleName:(NSString *)name toScale:(float)scaleSize;
//是否有缓存
-(BOOL)isCookie;
//设置返回按钮
+(UIButton *)getBackBtn:(NSString *)imgName title:(NSString *)name rect:(CGRect)size;
//按字号文字设置label
+(UILabel *)getCustomLabel:(NSString *)text font:(CGFloat) fontSize rect:(CGRect)labelRect;

+(UILabel *)getLabel:(NSString *)text font:(CGFloat) fontSize rect:(CGRect)labelRect;
//查看网络链接
+(BOOL) isConnectionAvailable;
//获取ip
+(NSString *)getIp;
//本地缓存
+(void)saveCache:(int)type andID:(int)_id andString:(NSString *)str;
//读取缓存
+(NSString *)getCache:(int)type andID:(int)_id;
+(void)ToastNotification:(NSString *)text andView:(UIView *)view andLoading:(BOOL)isLoading andIsBottom:(BOOL)isBottom;
//获取字典中值
+(NSString *) returnUserString:(NSString *)type;
+ (UIColor *)getBackgroundColor;
+ (UIColor *)getCellBackgroundColor;
+ (UIColor *)getColorForCell:(int)row;
//获取帐号密码
-(NSString *)getUserName;
-(NSString *)getPwd;
+ (void)ReleaseWebView:(UIWebView *)webView;

+(Helper *) Instance;
-(void)saveCookie:(BOOL)_isLogin;
-(NSString *)getUID;
+(NSString *)formatDate:(NSString*)dateStr;
+(void)saveUID:(NSString*)uid;
-(void)saveUserNameAndPwd:(NSString *)userName andPwd:(NSString *)pwd;
+ (void)clearWebViewBackground:(UIWebView *)webView;
+ (UserInfo *)getUserNotice:(ASIHTTPRequest *)request;
+ (ApiError *)getApiError:(ASIHTTPRequest *)request;
+ (void)TakeException:(NSException *)exception;
+ (void)CancelRequest:(ASIHTTPRequest *)request;
+ (NSString *)getOSVersion;
@end
