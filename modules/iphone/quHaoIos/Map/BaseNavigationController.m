//
//  BaseNavigationController.m
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "BaseNavigationController.h"

@interface BaseNavigationController ()

@end

@implementation BaseNavigationController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
#if IOS7_SDK_AVAILABLE
    UINavigationBar *navBar = [UINavigationBar appearance];
    [navBar setBackgroundColor:UIColorFromRGB(0x91d3f5)];
    [navBar setBarTintColor:UIColorFromRGB(0x91d3f5)];
    self.tabBarController.tabBar.translucent = NO;
    navBar.tintColor = [UIColor whiteColor];
#else
    if([self.navigationBar respondsToSelector:@selector(setBackgroundImage:forBarMetrics:) ]){
        CGSize size=CGSizeMake(kDeviceWidth,44);
        [self.navigationBar setBackgroundImage:[Helper reSizeImage:@"title.jpg" toSize:size] forBarMetrics:UIBarMetricsDefault];
    }
#endif
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

@implementation UINavigationBar (custom)

-(void)drawRect:(CGRect)rect{
   UIImage *image= [UIImage imageNamed:@"title.jpg"];
    [image drawInRect:rect ];
}

@end
