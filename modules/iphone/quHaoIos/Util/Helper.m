//
//  Helper.m
//  quHaoApp
//
//  Created by sam on 13-8-10.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "Helper.h"

@implementation Helper
@synthesize isLogin;
@synthesize viewBeforeLogin;
@synthesize viewNameBeforeLogin;

+ (void)showHUD:(NSString *)text andView:(UIView *)view andHUD:(MBProgressHUD *)hud
{
    [view addSubview:hud];
    hud.labelText = text;
    //    hud.dimBackground = YES;
    hud.square = YES;
    [hud show:YES];
}
+ (void)showHUD2:(NSString *)text andView:(UIView *)view andSize:(float)width
{
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:view animated:YES];
    hud.removeFromSuperViewOnHide =YES;
    //hud.mode = MBProgressHUDModeText;
    hud.labelText = NSLocalizedString(text, nil);
    hud.minSize = CGSizeMake(width, 90.0f);
    hud.square = YES;
    [hud hide:YES afterDelay:2];
}

+ (void)showHUD3:(NSString *)text andView:(UIView *)view
{
    MBProgressHUD *custuonHUD = [[MBProgressHUD alloc]initWithView:view];
    custuonHUD.customView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"37x-Checkmark.png"]];
    custuonHUD.labelText = @"发微博成功！";
    custuonHUD.mode = MBProgressHUDModeCustomView;
    [view addSubview:custuonHUD];
    [custuonHUD show:YES];
    [custuonHUD hide:YES afterDelay:2];
}

+ (void)arrowStyle:(UITableViewCell *)cell
{
    UIImage *image= [UIImage   imageNamed:@"arrow_right.png"];
    UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
    CGRect frame = CGRectMake(0.0, 0.0, image.size.width, image.size.height);
    button.frame = frame;
    [button setBackgroundImage:[self scaleImage:image toScale:1] forState:UIControlStateNormal];
    button.backgroundColor= [UIColor clearColor];
    cell.accessoryView = button;
}

+(UIImage *)reSizeImage:(NSString *)name toSize:(CGSize)reSize

{
    UIImage *image=[UIImage imageNamed:name];
    UIGraphicsBeginImageContext(CGSizeMake(reSize.width, reSize.height));
    [image drawInRect:CGRectMake(0, 0, reSize.width, reSize.height)];
    UIImage *reSizeImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return reSizeImage;
    
}

+(UIImage *)scaleImage:(UIImage *)image toScale:(float)scaleSize

{
    UIGraphicsBeginImageContext(CGSizeMake(image.size.width * scaleSize, image.size.height * scaleSize));
    [image drawInRect:CGRectMake(0, 0, image.size.width * scaleSize, image.size.height * scaleSize)];
    UIImage *scaledImage = UIGraphicsGetImageFromCurrentImageContext();
                                UIGraphicsEndImageContext();
                                
    return scaledImage;
}

+(UIImage *)scaleName:(NSString *)name toScale:(float)scaleSize

{
    UIImage *image=[UIImage imageNamed:name];
    UIGraphicsBeginImageContext(CGSizeMake(image.size.width * scaleSize, image.size.height * scaleSize));
    [image drawInRect:CGRectMake(0, 0, image.size.width * scaleSize, image.size.height * scaleSize)];
    UIImage *scaledImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return scaledImage;
    
}

-(BOOL)isCookie
{
    NSUserDefaults * setting = [NSUserDefaults standardUserDefaults];
    NSString * value = [setting objectForKey:@"cookie"];
    if (value && [value isEqualToString:@"1"]) {
        return YES;
    }
    else
    {
        return NO;
    }
}

+(UIButton *)getBackBtn:(NSString *)imgName title:(NSString *)name rect:(CGRect)size
{
    UIImage *backImage = [UIImage imageNamed:imgName];
    UIButton *backButton = [UIButton buttonWithType:UIButtonTypeCustom];
    backButton.bounds = size;
    [backButton setBackgroundImage:backImage forState:UIControlStateNormal];
    [backButton setTitle: name forState: UIControlStateNormal];
    backButton.titleLabel.font = [UIFont boldSystemFontOfSize:13.0f];         //设置button显示字体的大小
    //[backButton setTitleColor:[UIColor blackColor]forState:UIControlStateNormal];
    
    return backButton;
}

+(UILabel *)getCustomLabel:(NSString *)text font:(CGFloat) fontSize rect:(CGRect)labelRect
{
    UILabel *label = [[UILabel alloc] initWithFrame:labelRect];
    label.textAlignment = NSTextAlignmentLeft;
    label.text = text;
    label.font = [UIFont boldSystemFontOfSize:fontSize];
    label.backgroundColor=[UIColor clearColor];
    return label;
}

+(UILabel *)getLabel:(NSString *)text font:(CGFloat) fontSize rect:(CGRect)labelRect
{
    UILabel *label = [[UILabel alloc] initWithFrame:labelRect];
    label.textAlignment = NSTextAlignmentLeft;
    label.text = text;
    label.font = [UIFont systemFontOfSize:fontSize];
    label.backgroundColor=[UIColor clearColor];
    return label;
}

+(BOOL) isConnectionAvailable{
    
    BOOL isExistenceNetwork = YES;
    Reachability *reach = [Reachability reachabilityWithHostName:@"www.baidu.com"];
    switch ([reach currentReachabilityStatus]) {
        case NotReachable:
            isExistenceNetwork = NO;
            //NSLog(@"notReachable");
            break;
        case ReachableViaWiFi:
            isExistenceNetwork = YES;
            //NSLog(@"WIFI");
            break;
        case ReachableViaWWAN:
            isExistenceNetwork = YES;
            //NSLog(@"3G");
            break;
    }
    return isExistenceNetwork;
}

+(NSString *)getIp
{
    NSString *ip=nil;
    if([NSUserName() compare:@"sam"]==0){
        ip=@"http://192.168.2.101:9081";
    }else{
        ip=@"http://localhost:9081";

    }
    return ip;
}

//本地缓存
+ (void)saveCache:(int)type andID:(int)_id andString:(NSString *)str
{
    NSUserDefaults * setting = [NSUserDefaults standardUserDefaults];
    NSString * key = [NSString stringWithFormat:@"detail-%d-%d",type, _id];
    [setting setObject:str forKey:key];
    [setting synchronize];
}
//读取缓存
+ (NSString *)getCache:(int)type andID:(int)_id
{
    NSUserDefaults * settings = [NSUserDefaults standardUserDefaults];
    NSString *key = [NSString stringWithFormat:@"detail-%d-%d",type, _id];
    
    NSString *value = [settings objectForKey:key];
    return value;
}

+ (void)ToastNotification:(NSString *)text andView:(UIView *)view andLoading:(BOOL)isLoading andIsBottom:(BOOL)isBottom
{
    GCDiscreetNotificationView *notificationView = [[GCDiscreetNotificationView alloc] initWithText:text showActivity:isLoading inPresentationMode:isBottom?GCDiscreetNotificationViewPresentationModeBottom:GCDiscreetNotificationViewPresentationModeTop inView:view];
    [notificationView show:YES];
    [notificationView hideAnimatedAfter:2.6];
}

+ (NSString *) returnUserString:(NSString *)type{
    return [[NSUserDefaults standardUserDefaults] objectForKey:type];
}

+ (UIColor *)getBackgroundColor
{
    return [UIColor whiteColor];
}
+ (UIColor *)getCellBackgroundColor
{
    return [UIColor colorWithRed:235.0/255 green:235.0/255 blue:243.0/255 alpha:1.0];
}


+ (UIColor *)getColorForCell:(int)row
{
    return row % 2 ?
    [UIColor colorWithRed:235.0/255.0 green:242.0/255.0 blue:252.0/255.0 alpha:1.0]:
    [UIColor colorWithRed:248.0/255.0 green:249.0/255.0 blue:249.0/255.0 alpha:1.0];
}

+ (NSString *)getUserName
{
    NSUserDefaults * settings = [NSUserDefaults standardUserDefaults];
    return [settings objectForKey:@"UserName"];
}
+ (NSString *)getPwd
{
    NSUserDefaults * settings = [NSUserDefaults standardUserDefaults];
    NSString * temp = [settings objectForKey:@"Password"];
    return [AESCrypt decrypt:temp password:@"pwd"];
}

-(void)saveCookie:(BOOL)_isLogin
{
    NSUserDefaults * setting = [NSUserDefaults standardUserDefaults];
    [setting removeObjectForKey:@"cookie"];
    [setting setObject:_isLogin ? @"1" : @"0" forKey:@"cookie"];
    [setting synchronize];
}

+ (void)clearWebViewBackground:(UIWebView *)webView
{
    UIWebView *web = webView;
    for (id v in web.subviews) {
        if ([v isKindOfClass:[UIScrollView class]]) {
            [v setBounces:NO];
        }
    }
}
-(void)saveUserNameAndPwd:(NSString *)userName andPwd:(NSString *)pwd
{
    NSUserDefaults * settings = [NSUserDefaults standardUserDefaults];
    [settings removeObjectForKey:@"UserName"];
    [settings removeObjectForKey:@"Password"];
    [settings setObject:userName forKey:@"UserName"];
    
    pwd = [AESCrypt encrypt:pwd password:@"pwd"];
    
    [settings setObject:pwd forKey:@"Password"];
    [settings synchronize];
}

+(NSString *)formatDate:(NSString*)dateStr
{
    NSDateFormatter *inputFormatter = [[NSDateFormatter alloc] init];
    [inputFormatter setLocale:[[NSLocale alloc] initWithLocaleIdentifier:@"en_US"]];
    [inputFormatter setDateFormat:@"MMM d,yyyy hh:mm:ss aa"];
    NSDate* inputDate = [inputFormatter dateFromString:dateStr];

    NSDateFormatter *outputFormatter = [[NSDateFormatter alloc] init];
    [outputFormatter setLocale:[NSLocale currentLocale]];
    [outputFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
       
    return  [outputFormatter stringFromDate:inputDate];
}

+ (void)ReleaseWebView:(UIWebView *)webView
{
    [webView stopLoading];
    [webView setDelegate:nil];
    webView = nil;
}

+(void)saveUID:(NSString*)uid
{
    NSUserDefaults *setting = [NSUserDefaults standardUserDefaults];
    [setting removeObjectForKey:@"UID"];
    [setting setObject:uid forKey:@"UID"];
    [setting synchronize];
}

-(NSString *)getUID
{
    NSUserDefaults * settings = [NSUserDefaults standardUserDefaults];
    return [settings objectForKey:@"UID"];
}

+ (UserInfo *)getUserNotice:(ASIHTTPRequest *)request
{
    NSString *response = [request responseString];
    return [Helper getUserNotice2:response];
}
+ (UserInfo *)getUserNotice2:(NSString *)response
{
        SBJsonParser *jsonParser = [[SBJsonParser alloc] init];
        NSError *error = nil;
        NSArray *jsonObjects = [jsonParser objectWithString:response error:&error];
        if (!jsonObjects) {
            [Helper Instance].isLogin = NO;
            [[NSNotificationCenter defaultCenter] postNotificationName:@"login" object:@"0"];
            return nil;
        }else{
            [[NSNotificationCenter defaultCenter] postNotificationName:@"login" object:@"1"];
            [Helper Instance].isLogin = YES;
        }
        
        NSString * name=[jsonObjects valueForKey:@"nickname"];
        NSString * jifen=[jsonObjects valueForKey:@"jifen"];
        NSString * signIn=[jsonObjects valueForKey:@"signIn"];
        NSString * dianping=[jsonObjects valueForKey:@"dianping"];
        NSString * phone=[jsonObjects valueForKey:@"phone"];
        NSString * accountId=[jsonObjects valueForKey:@"accountId"];
        NSString * userImage=[jsonObjects valueForKey:@"userImage"];
        NSString * isSignIn=[jsonObjects valueForKey:@"isSignIn"];

        UserInfo *ui = [UserInfo alloc];
        ui.username=name;
        ui.phone=phone;
        ui.jifen=[jifen intValue];
        ui.signIn=[signIn intValue];
        ui.dianping=[dianping intValue];
        ui.accountId=accountId;
        ui.userImage=userImage;
        ui.isSignIn=[isSignIn boolValue];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"Notification_NoticeUpdate" object:ui];
    
        [Helper saveUID:accountId];
        return ui;
    
}
+ (ApiError *)getApiError:(ASIHTTPRequest *)request
{
    return [Helper getApiError2:request.responseString];
}
+ (ApiError *)getApiError2:(NSString *)response
{
    @try {
        SBJsonParser *jsonParser = [[SBJsonParser alloc] init];
        NSError *error = nil;
        NSArray *jsonObjects = [jsonParser objectWithString:response error:&error];
        if (jsonObjects==nil) {
            return nil;
        }        
        NSString * errorCode=[jsonObjects valueForKey:@"errorCode"];
        
        return [[ApiError alloc] initWithParameters:[errorCode intValue]];
    }
    @catch (NSException *exception) {
        [Helper TakeException:exception];
        return [[ApiError alloc] initWithParameters:-1 andMessage:@"出现异常"];
    }
    @finally {
        //        return [[ApiError alloc] initWithParameters:-1 andMessage:@"出现异常"];
    }
}

+ (void)TakeException:(NSException *)exception
{
    NSArray * arr = [exception callStackSymbols];
    NSString * reason = [exception reason];
    NSString * name = [exception name];
    NSString * url = [NSString stringWithFormat:@"========异常错误报告========\nname:%@\nreason:\n%@\ncallStackSymbols:\n%@",name,reason,[arr componentsJoinedByString:@"\n"]];
    NSString * path = [applicationDocumentsDirectory() stringByAppendingPathComponent:@"Exception.txt"];
    [url writeToFile:path atomically:YES encoding:NSUTF8StringEncoding error:nil];
}

NSString * applicationDocumentsDirectory()
{
    return [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
}


+ (void)CancelRequest:(ASIHTTPRequest *)request
{
    if (request != nil) {
        [request cancel];
        [request clearDelegatesAndCancel];
        
    }
}
+ (NSString *)getOSVersion
{
    return [NSString stringWithFormat:@"QuhaoiPhone/%@/%@/%@/%@",AppVersion,[UIDevice currentDevice].systemName,[UIDevice currentDevice].systemVersion, [UIDevice currentDevice].model];
}
static Helper * instance = nil;
+(Helper *) Instance
{
    @synchronized(self)
    {
        if(nil == instance)
        {
            [self new];
        }
    }
    return instance;
}
@end
