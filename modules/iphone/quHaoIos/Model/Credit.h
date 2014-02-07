//
//  Credit.h
//  quHaoIos
//
//  Created by sam on 13-11-24.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Credit : NSObject

@property(nonatomic,strong) NSString * merchantId ;
@property(nonatomic,strong) NSString * merchantName ;
@property(nonatomic,strong) NSString * merchantAddress ;
@property(nonatomic,strong) NSString * reservationId;
@property (nonatomic,strong) NSString *seatNumber;
@property (nonatomic,strong) NSString *myNumber;
@property BOOL cost;
@property (nonatomic,strong) NSString *status;
@property int jifen;
@property (nonatomic,strong) NSString *created;

@end
