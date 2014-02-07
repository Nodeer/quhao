//
//  SettingModel.h
//  quHaoApp
//
//  Created by sam on 13-9-30.
//  Copyright (c) 2013年 sam. All rights reserved.
//
#import <Foundation/Foundation.h>

@interface SettingModel : NSObject

@property (copy,nonatomic) NSString * img;
@property (copy,nonatomic) NSString * title;
@property (copy,nonatomic) NSString * title2;
@property NSUInteger tag;

- (id)initWith:(NSString *)_title andImg:(NSString *)_img andTag:(NSUInteger)_tag andTitle2:(NSString *)_title2;

@end
