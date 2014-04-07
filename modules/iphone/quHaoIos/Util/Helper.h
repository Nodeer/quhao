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
+ (UIImage*)imageWithImageSimple:(UIImage*)image scaledToSize:(CGSize)newSize;
//文件是否存在
+ (BOOL) isFileExist:(NSString *)fileName;
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
//配置View背景色
+ (UIColor *)getBackgroundColor;
//配置cell背景色
+ (UIColor *)getCellBackgroundColor;
//按行配置cell背景色
+ (UIColor *)getColorForCell:(int)row;
//获取帐号密码
+(NSString *)getUserName;
+(NSString *)getPwd;
//保存帐号密码
-(void)saveUserNameAndPwd:(NSString *)userName andPwd:(NSString *)pwd;
//保存登陆状态
-(void)saveCookie:(BOOL)_isLogin;
//获取UID
-(NSString *)getUID;
//保存UID
+(void)saveUID:(NSString*)uid;
//格式化时间
+(NSString *)formatDate:(NSString*)dateStr;
+ (void)ReleaseWebView:(UIWebView *)webView;
+ (void)clearWebViewBackground:(UIWebView *)webView;
+ (UserInfo *)getUserNotice:(ASIHTTPRequest *)request;
+ (ApiError *)getApiError:(ASIHTTPRequest *)request;
+ (ApiError *)getApiError2:(NSString *)response;
+ (void)TakeException:(NSException *)exception;
+ (void)CancelRequest:(ASIHTTPRequest *)request;
+ (NSString *)getOSVersion;
+(Helper *) Instance;
@end
