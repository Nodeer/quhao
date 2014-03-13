//
//  Reservation.h
//  quHaoIos
//
//  Created by sam on 13-9-22.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Reservation : NSObject

@property(nonatomic,strong) NSString * id ;
@property(nonatomic,strong) NSString * name ;
@property(nonatomic,strong) NSString * tipValue ;
@property(nonatomic,strong) NSString * accountId;
@property (nonatomic,strong) NSString *merchantId;
@property(nonatomic,strong)NSString* seatNumber;
@property(nonatomic,strong)NSString*  myNumber ;
@property(nonatomic,strong)NSString*  currentNumber ;
@property (nonatomic,strong) NSString *imgUrl;
@property BOOL isCommented;
@property int  beforeYou ;

@end
