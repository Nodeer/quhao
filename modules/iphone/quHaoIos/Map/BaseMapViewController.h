//
//  BaseMapViewController.h
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MAMapKit/MAMapKit.h>
#define kBMLineWidthDefault 5.f

@interface BaseMapViewController : UIViewController<MAMapViewDelegate>

@property (nonatomic, strong) MAMapView *mapView;
@property  double x;
@property  double y;

@end

