//
//  CustomAnnotation.m
//  quHaoIos
//
//  Created by sam on 14-4-29.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "CustomAnnotation.h"

@implementation CustomAnnotation

- (id) initWithCoordinates:(CLLocationCoordinate2D)paramCoordinates title:(NSString *)paramTitle subTitle:(NSString *)paramSubTitle
{
    if (self = [super init]) {
        _coordinate = paramCoordinates;
        _title = [paramTitle copy];
        _subtitle = [paramSubTitle copy];
    }
    
    return self;
}

@end
