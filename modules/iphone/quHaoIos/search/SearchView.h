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
    NSMutableArray * results;
    BOOL isLoading;
    BOOL isLoadOver;
    
    int allCount;
}
@property (strong, nonatomic) IBOutlet UITableView *tableResult;
@property (strong, nonatomic) IBOutlet UISearchBar *_searchBar;

-(void)doSearch;
-(void)clear;


@end
