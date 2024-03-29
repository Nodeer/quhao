//
//  Message.h
//  quHaoIos
//
//  Created by sam on 14-5-29.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <Foundation/Foundation.h>


typedef enum {
    
    MessageTypeMe = 0, // 自己发的
    MessageTypeOther = 1,//别人发得
    MessageTypeuser = 2 
} MessageType;

@interface Message : NSObject

@property (nonatomic, copy) NSString *icon;
@property (nonatomic, copy) NSString *time;
@property (nonatomic, copy) NSString *content;
@property (nonatomic, assign) MessageType type;

@property (nonatomic, copy) NSDictionary *dict;

@end
