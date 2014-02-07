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
    if([self.navigationBar respondsToSelector:@selector(setBackgroundImage:forBarMetrics:) ]){
        [self.navigationBar setBackgroundImage:[UIImage imageNamed:@"nav_all_bg"] forBarMetrics:UIBarMetricsDefault];
    }

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

@implementation UINavigationBar (custom)

-(void)drawRect:(CGRect)rect{
   UIImage *image= [UIImage imageNamed:@"nav_all_bg"];
    [image drawInRect:rect ];
}

@end
