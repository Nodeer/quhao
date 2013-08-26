//
//  CommonHTTPRequest.m
//  quhaoIOS
//
//  Created by cross on 13-8-26.
//  Copyright (c) 2013年 withiter. All rights reserved.
//

#import "CommonHTTPRequest.h"
#import "SBJson.h"

@implementation CommonHTTPRequest

+(id) request{
    // http request
    NSURL *url = [NSURL URLWithString:@"http://192.168.1.8:9081/login"];
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    [request startSynchronous];
    NSError *httpError = [request error];
    NSString *response = @"";
    if (!httpError) {
        response = [request responseString];
        //        NSLog(@"%@", response);
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"网络不是很好，请稍后再试" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
    }
}

@end
