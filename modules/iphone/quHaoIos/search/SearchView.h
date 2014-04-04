//
//  AppDelegate.h
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//
#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"
#import "MerchartDetail.h"
@interface SearchView : UIViewController<UITableViewDelegate,UITableViewDataSource,UISearchBarDelegate>
{
    NSMutableArray * _results;
    BOOL _isLoading;
    BOOL _isLoadOver;
    
    int _allCount;
}
@property (strong, nonatomic) IBOutlet UITableView *tableResult;
@property (strong, nonatomic) IBOutlet UISearchBar *searchBar;

-(void)doSearch;
-(void)clear;


@end
