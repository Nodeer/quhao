//
//  CityViewController.h
//  quHaoIos
//
//  Created by sam on 14-3-29.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Helper.h"
#import <CoreLocation/CoreLocation.h>
#import <CoreLocation/CLLocationManagerDelegate.h>
#import <QuartzCore/QuartzCore.h>
#import "WGS84TOGCJ02.h"

@interface CityViewController : UIViewController<UITableViewDelegate,UITableViewDataSource,UISearchBarDelegate,CLLocationManagerDelegate>
{
    NSMutableDictionary *cities;
    NSMutableArray *keys;
    UITableView *tbView;
    UILabel * lable;
    UIButton * button;
}
@property (strong, nonatomic)  UITableView *tbView;
@property (nonatomic, retain) NSMutableDictionary *cities;
@property (nonatomic, retain) NSMutableArray *keys;
@property (nonatomic, retain) NSMutableArray * tempArray;
@property (nonatomic, retain) NSMutableArray * codeArray;
@property (nonatomic, assign) id delegate;
@property (strong, nonatomic) UISearchBar *searchBar;
@property(nonatomic, assign) BOOL isSearch;//是否是search状态
@property(nonatomic, strong) NSString * loctionCity;//定位城市
@property(nonatomic, strong) NSString * loctionCityCode;//定位城市code
@property(nonatomic, strong) CLLocationManager *locationManager;

@end

@protocol CityViewDelegate
- (void) citySelectionUpdate:(NSString*)selectedCity withCode:(NSString*) code;
- (NSString*) getDefaultCity;
@end



