//
//  BaseSearchViewController.m
//  Category_demo
//
//  Created by songjian on 13-3-22.
//  Copyright (c) 2013年 songjian. All rights reserved.
//

#import "BaseSearchViewController.h"

@implementation BaseSearchViewController
@synthesize search = _search;
@synthesize ownMapView = _ownMapView;

#pragma mark - MASearchDelegate

-(void)search:(id)searchOption Error:(NSString*)errCode
{
    NSLog(@"%s: searchOption = %@, errCode = %@", __func__, [searchOption class], errCode);
}

#pragma mark - Life Cycle

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    self.search.delegate = self;
}

- (void)viewDidDisappear:(BOOL)animated
{
    self.search.delegate = nil;
    
    [super viewDidDisappear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    
    /* Release memory related with search. */
}

@end
