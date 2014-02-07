//
//  BaseSearchViewController.h
//  Category_demo
//
//  Created by songjian on 13-3-22.
//  Copyright (c) 2013年 songjian. All rights reserved.
//

#import "BaseMapViewController.h"
#import "MASearchKit.h"

#define SearchKey @"7e5d17f015e4c22936fc3fd7341a903d"

@interface BaseSearchViewController : UITableViewController<MAMapViewDelegate,MASearchDelegate>
@property (nonatomic, strong) MASearch *search;
@property (nonatomic, strong) MAMapView *ownMapView;

@end
