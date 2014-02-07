//
//  QuHaoUtil.h
//  quHaoIos
//
//  Created by sam on 14-1-22.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QuHaoUtil : NSObject

+(NSString *)returnFormatString:(NSString *)string;

+(NSString *)requestDb:(NSString *)string;

+(NSArray *)analyseData:(NSString *)responseStr;

+(NSDictionary *)analyseDataToDic:(NSString *)responseStr;

+(NSString *) returnOAuthUrlString;
- (void) getAccessToken : (NSString *) code;
+ (BOOL)isAuthValid;
@end
