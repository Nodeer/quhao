//
//  UIScrollView+ScrollViewCategory.h
//  quHaoIos
//
//  Created by sam on 14-3-4.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIScrollView (ScrollViewCategory)

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event;
- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event;
- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event;

@end
