//
//  MerchartModel.h
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface MerchartModel : NSObject
@property(nonatomic,strong) NSString * id ;
@property (nonatomic,strong) NSString * name ;
@property float averageCost;
@property float xingjiabi;
@property float kouwei;
@property float huanjing;
@property float fuwu;
@property (nonatomic,strong) NSString * address;
@property (nonatomic,strong) NSArray *telephone;
@property (nonatomic,strong) NSString *openTime;
@property (nonatomic,strong) NSString *closeTime;
@property (nonatomic,strong) NSString *cateType;
@property (nonatomic,strong) NSString *imgUrl;
@property (nonatomic,strong) NSString *description;
@property (nonatomic,strong) NSArray *seatType;
@property  double x;
@property  double y;
@property (nonatomic,strong) NSArray * tags ;
//商家距离中心点的距离
@property (nonatomic,strong) NSString *distance;
//poi查询返回的商家的唯一id
@property (nonatomic,strong) NSString *pguid;
@property (nonatomic,strong) NSString *commentContent;
//是否被关注
@property BOOL isAttention;
//是否已经开通
@property BOOL enable;

@end
