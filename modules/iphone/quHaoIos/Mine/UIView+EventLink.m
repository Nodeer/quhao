//
//  UIView+EventLink.m
//  quHaoIos
//
//  Created by sam on 14-5-25.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "UIView+EventLink.h"

@implementation UIView (EventLink)
-(UIViewController *)viewController{
    UIResponder *nexRes=[self nextResponder];
    
    do {
        //判读当前的响应者是否UIViewController
        if ([nexRes isKindOfClass:[UIViewController class]]) {
            //是否直接处理
            return  (UIViewController*)nexRes;
        }else{
            //否则继续寻找
            nexRes=[nexRes nextResponder];
            
        }
    } while (nexRes!=nil);
    return nil;
}
@end
