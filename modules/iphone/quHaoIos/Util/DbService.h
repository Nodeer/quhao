//
//  DbService.h
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface DbService : NSObject
+(id)parseData:(NSString *)name;
+(id)getHomeData;
+(id)parseJson:(NSData *)data;

@end
