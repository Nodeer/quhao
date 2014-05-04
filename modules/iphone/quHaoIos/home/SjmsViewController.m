//
//  SjmsViewController.m
//  quHaoIos
//
//  Created by sam on 14-5-4.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "SjmsViewController.h"

@interface SjmsViewController ()

@end

@implementation SjmsViewController
@synthesize sjms;
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
    
    self.navigationItem.title = @"商家描述";
    
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 5, 50, 30 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    self.view.backgroundColor  = [ UIColor whiteColor];
    
    UILabel *contextLabel = [[UILabel alloc] initWithFrame:CGRectMake( 10, 15, kDeviceWidth-20, kDeviceHeight )];
    contextLabel.backgroundColor = [UIColor clearColor];
    contextLabel.numberOfLines = 0;
    contextLabel.textColor = [UIColor darkGrayColor];
    contextLabel.font = [UIFont fontWithName:@"Arial" size:13.0];
    contextLabel.text = self.sjms;
    [contextLabel sizeToFit];
    [self.view addSubview:contextLabel];

}

- (void)clickToHome:(id)sender
{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

@end
