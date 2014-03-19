//
//  BaseSearchViewController.h
//  Category_demo
//
//  Created by songjian on 13-3-22.
//  Copyright (c) 2013年 songjian. All rights reserved.
//

#import "BaseMapViewController.h"
#import <AMapSearchKit/AMapSearchAPI.h>

@interface BaseSearchViewController : UITableViewController<MAMapViewDelegate,AMapSearchDelegate>
@property (nonatomic, strong) AMapSearchAPI *search;
@property (nonatomic, strong) MAMapView *ownMapView;

@end
