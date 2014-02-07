//
//  SettingModel.m
//  quHaoApp
//
//  Created by sam on 13-9-30.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "SettingModel.h"

@implementation SettingModel

@synthesize img;
@synthesize title;
@synthesize title2;
@synthesize tag;

- (id)initWith:(NSString *)_title andImg:(NSString *)_img andTag:(NSUInteger)_tag andTitle2:(NSString *)_title2;
{
    SettingModel *result = [[SettingModel alloc] init];
    result.title = _title;
    result.img = _img;
    result.tag = _tag;
    result.title2 = _title2;
    return result;
}

@end
