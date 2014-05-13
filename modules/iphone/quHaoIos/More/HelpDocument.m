//
//  HelpDocument.m
//  quHaoIos
//
//  Created by sam on 14-4-3.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "HelpDocument.h"

@implementation HelpDocument

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = @"帮助";
   
    UIButton *backButton=[Helper getBackBtn:@"back"];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    
    self.view.backgroundColor  = [ UIColor whiteColor];
    _sc = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, kDeviceWidth, kDeviceHeight-64)];
    _sc.contentSize = CGSizeMake(kDeviceWidth, kDeviceHeight);
    _sc.scrollEnabled = YES;
    _sc.backgroundColor  = [ UIColor whiteColor];
    [self.view addSubview:_sc];
    _titles = @[@"积分有什么用？",@"如何获得积分？",@"关于签到？"];
    _contexts = @[@"在商家取号是要消耗一定积分的，没有积分是不能取号的。",@"在商家成功消费后，会返回一定积分；另外可以通过签到，完成任务等可以获得一定的积分。",
                  @"每天可签到一次，满五次会增加一个积分"];
    [self labelFactory];
}

- (void)labelFactory
{
    CGRect sectionFrame;
    CGFloat width = self.view.frame.size.width;
    CGFloat xOffset = 10;
    CGFloat height = 20.0;
    
    for (int i=0; i < _titles.count; i++) {
        id item = [_titles objectAtIndex:i];
        
        sectionFrame = CGRectMake(xOffset, height, width - xOffset, 0.0);
        UILabel *textLabel = [[UILabel alloc] initWithFrame:sectionFrame];
        textLabel.backgroundColor = [UIColor clearColor];
        textLabel.numberOfLines = 0;
        textLabel.textColor = [UIColor darkGrayColor];
        textLabel.font = [UIFont boldSystemFontOfSize:14.0];
        textLabel.text = [NSString stringWithFormat:@"%d.%@", i+1 , item ];
        [textLabel sizeToFit];
        height += textLabel.bounds.size.height + 10;
        [self.view addSubview:textLabel];
        
        
        sectionFrame = CGRectMake(xOffset+10, height, width - xOffset-15, 0.0);
        UILabel *contextLabel = [[UILabel alloc] initWithFrame:sectionFrame];
        contextLabel.backgroundColor = [UIColor clearColor];
        contextLabel.numberOfLines = 0;
        contextLabel.textColor = [UIColor darkGrayColor];
        contextLabel.font = [UIFont fontWithName:@"Arial" size:13.0];
        contextLabel.text = [_contexts objectAtIndex:i];
        [contextLabel sizeToFit];
        
        //CGFloat sectionSpace = i < self.items.count - 1 ? self.itemsSpacing : 0.0;
        height += contextLabel.bounds.size.height + 10;
        //sectionFrame = CGRectMake(xOffset, height, width - xOffset, 0.0);
        [self.view addSubview:contextLabel];
    }
    _sc.contentSize = CGSizeMake(kDeviceWidth, height);
}

- (void)clickToHome:(id)sender
{
    [self.navigationController popToRootViewControllerAnimated:YES];
}
@end
