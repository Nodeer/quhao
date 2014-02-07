//
//  QuHaoUtil.m
//  quHaoIos
//
//  Created by sam on 14-1-22.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "QuHaoUtil.h"
#import "ASIHTTPRequest.h"
#import "SBJsonParser.h"
#import "Helper.h"
@implementation QuHaoUtil

+(NSString *)returnFormatString:(NSString *)string
{
    return [string stringByReplacingOccurrencesOfString:@" " withString:@"%20"];
}

+(NSString *)requestDb:(NSString *)string
{
    NSString *str1= [NSString stringWithFormat:@"%@", string];
    NSURL *url=[NSURL URLWithString:str1];
    
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    [request startSynchronous];
    NSError *httpError = [request error];
    NSString *response = @"";
    if (!httpError) {
        response = [request responseString];
    }
    return response;
}

+(NSArray *)analyseData:(NSString *)responseStr
{
    SBJsonParser *jsonParser = [[SBJsonParser alloc] init];
    NSError *error = nil;
    NSArray *jsonObjects = nil;
    jsonObjects = [jsonParser objectWithString:responseStr error:&error];
    if(error!=nil){
        jsonObjects=nil;
    }
    return jsonObjects;
}

+(NSDictionary *)analyseDataToDic:(NSString *)responseStr
{
    SBJsonParser *jsonParser = [[SBJsonParser alloc] init];
    NSError *error = nil;
    NSDictionary *jsonObjects = nil;    
    jsonObjects = [jsonParser objectWithString:responseStr error:&error];
    if(error!=nil){
        jsonObjects=nil;
    }
    return jsonObjects;
}

+(NSString *) returnOAuthUrlString
{
    return [NSString stringWithFormat:@"%@?client_id=%@&redirect_uri=%@&response_type=code&display=mobile&state=authorize",OAuth_URL,kAppKey,kRedirectURI];
}

- (void) getAccessToken : (NSString *) code{
    
    //access token调用URL的string
    NSMutableString *accessTokenUrlString = [[NSMutableString alloc] initWithFormat:@"%@?client_id=%@&client_secret=%@&grant_type=authorization_code&redirect_uri=%@&code=",ACCESS_TOKEN_URL,kAppKey,APP_SECRET,kRedirectURI];
    [accessTokenUrlString appendString:code];
    
    //同步POST请求
    NSURL *urlstring = [NSURL URLWithString:accessTokenUrlString];
    //第二步，创建请求
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc]initWithURL:urlstring cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:10];
    [request setHTTPMethod:@"POST"];//设置请求方式为POST，默认为GET
    
    //第三步，连接服务器
    NSData *received = [NSURLConnection sendSynchronousRequest:request returningResponse:nil error:nil];
    NSString *backString = [[NSString alloc]initWithData:received encoding:NSUTF8StringEncoding];
    
    //如何从backString中获取到access_token
    //NSDictionary *dictionary = [backString objectFromJSONString];
    NSDictionary *dictionary=[QuHaoUtil analyseDataToDic:backString];

    [[NSUserDefaults standardUserDefaults] setObject:[dictionary objectForKey:@"access_token"] forKey:@"access_token"];
    [[NSUserDefaults standardUserDefaults] setObject:[dictionary objectForKey:@"uid"] forKey:@"wb_uid"];

    [[NSUserDefaults standardUserDefaults] synchronize];
}

/**
 * @description 判断微博是否登录
 * @return YES为已登录；NO为未登录
 */
+ (BOOL)isLoggedIn
{
    return [[NSUserDefaults standardUserDefaults] objectForKey:@"wb_uid"] && [[NSUserDefaults standardUserDefaults] objectForKey:@"access_token"]  ;
}

/**
 * @description 判断微博登录是否有效，当已登录并且登录未过期时为有效状态
 * @return YES为有效；NO为无效
 */
+ (BOOL)isAuthValid
{
    return [self isLoggedIn];
}
@end
