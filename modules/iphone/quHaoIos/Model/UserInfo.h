//
//  UserInfo.h
//  quHaoIos
//
//  Created by sam on 13-10-20.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface UserInfo : NSObject

@property(nonatomic,strong) NSString *accountId;
@property(nonatomic,strong) NSString * username;
@property int  jifen;
@property int  signIn;
@property int  dianping;
@property int guanzhu;
@property BOOL isSignIn;
@property(nonatomic,strong) NSString *phone;
@property(nonatomic,strong) NSString *userImage;
@property(nonatomic,strong) NSString *imgUrl;

@end
