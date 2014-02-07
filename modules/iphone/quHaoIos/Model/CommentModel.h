//
//  CommentModel.h
//  quHaoIos
//
//  Created by sam on 13-12-8.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CommentModel : NSObject

@property(nonatomic,strong) NSString * id ;
@property(nonatomic,strong) NSString * nickName ;
@property(nonatomic,strong) NSString * merchantName;
@property (nonatomic,strong) NSString *averageCost;
@property int xingjiabi;
@property int kouwei;
@property int huanjing;
@property int fuwu;
@property int grade;
@property (nonatomic,strong) NSString *content;
@property (nonatomic,strong) NSString *created;
@end

