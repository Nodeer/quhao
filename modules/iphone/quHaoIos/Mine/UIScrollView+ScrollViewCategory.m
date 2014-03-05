//
//  UIScrollView+ScrollViewCategory.m
//  quHaoIos
//
//  Created by sam on 14-3-4.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "UIScrollView+ScrollViewCategory.h"

@implementation UIScrollView (ScrollViewCategory)

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [[self nextResponder] touchesBegan:touches withEvent:event];
    [super touchesBegan:touches withEvent:event];
}
- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
    [[self nextResponder] touchesMoved:touches withEvent:event];
    [super touchesMoved:touches withEvent:event];
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
    [[self nextResponder] touchesEnded:touches withEvent:event];
    [super touchesEnded:touches withEvent:event];
}

@end
