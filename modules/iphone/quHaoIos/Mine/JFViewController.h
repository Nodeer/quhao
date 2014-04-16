//
//  JFViewController.h
//  quHaoIos
//
//  Created by sam on 14-4-15.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Helper.h"
@interface JFViewController : UIViewController
{
    NSArray *_titles;
    NSArray *_contexts;
}

//生成label
- (void)labelFactory;

@end
