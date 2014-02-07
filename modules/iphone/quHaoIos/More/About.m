//
//  About.m
//  quHaoApp
//
//  Created by sam on 13-9-30.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "About.h"

@implementation About
@synthesize lblVersion;

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
        
    self.lblVersion.text = [NSString stringWithFormat:@"版本: %@", @"1.0.1"];
    
    self.navigationItem.title = @"关于我们";
    
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 7, 50, 35 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

- (void)viewDidUnload
{
    [self setLblVersion:nil];
    [self setLblOSC:nil];
    [self setLblVersion:nil];
    [self setLblCopyright:nil];
    [self setImg:nil];
    [super viewDidUnload];
}

@end
