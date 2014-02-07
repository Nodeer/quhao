//
//  BaseNavigationController.h
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface BaseNavigationController : UINavigationController

@end

@interface UINavigationController(custom)
-(void)drawRect:(CGRect)rect;
@end
