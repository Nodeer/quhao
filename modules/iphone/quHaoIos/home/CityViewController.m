//
//  CityViewController.m
//  quHaoIos
//
//  Created by sam on 14-3-29.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "CityViewController.h"

@interface CityViewController ()

@property NSUInteger curSection;
@property NSUInteger curRow;
@end

@implementation CityViewController
@synthesize tbView,loctionCity;
@synthesize locationManager;
@synthesize cities, tempArray,codeArray,keys ,loctionCityCode, curSection, curRow, delegate;

-(void)loadNavigationItem
{
    self.view.backgroundColor=[UIColor whiteColor];
    
    UIButton *backButton=[Helper getBackBtn:@"button.png" title:@"关 闭" rect:CGRectMake( 0, 0, 40, 25 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self loadNavigationItem];
    UIView *view1 = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, kDeviceWidth, kDeviceHeight)];
    view1.backgroundColor = [UIColor whiteColor];
    self.view = view1;
    
    self.isSearch = NO;
    self.tempArray = [[NSMutableArray alloc] init];//search出来的数据存放 name
    self.codeArray = [[NSMutableArray alloc] init];//search出来的数据存放 code

    _searchBar = [[UISearchBar alloc]initWithFrame:CGRectMake(0, 0, kDeviceWidth, 40)];
    _searchBar.delegate = self;   //设置控件
    _searchBar.placeholder = @"请输入城市或者首字母查询";
    _searchBar.translucent = YES;
    
    [self.view addSubview:_searchBar];
    
    UIImage *backImage = [UIImage imageNamed:@"button"];
    button = [UIButton buttonWithType:UIButtonTypeCustom];
    button.frame = CGRectMake(kDeviceWidth-44, 44, 40, 30);
    [button setBackgroundImage:backImage forState:UIControlStateNormal];
    button.titleLabel.font = [UIFont boldSystemFontOfSize:13.0f];
    [button setTitle:@"定位" forState:UIControlStateNormal];
    button.enabled = NO;
    [button addTarget:self action:@selector(reLoadLoction:) forControlEvents:UIControlEventTouchUpInside];
    
    lable = [[UILabel alloc] initWithFrame:CGRectMake(5, 44, 220, 30)];
    lable.font = [UIFont systemFontOfSize:15];
    lable.text = @"正在定位...";
    UITapGestureRecognizer *tapGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(onLocationLable:)];
    lable.userInteractionEnabled=YES;
    [lable addGestureRecognizer:tapGesture];
    [self.view addSubview:button];
    [self.view addSubview:lable];
    
    [self locationService];
    
    tbView=[[UITableView alloc] initWithFrame:CGRectMake(0, 74, kDeviceWidth, kDeviceHeight-134)];
    tbView.backgroundColor = [UIColor whiteColor];
    tbView.delegate=self;
    tbView.dataSource=self;
    [self.view addSubview:tbView];
    tbView.separatorStyle = NO;
#if IOS7_SDK_AVAILABLE
    self.edgesForExtendedLayout = UIRectEdgeNone;
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.modalPresentationCapturesStatusBarAppearance = NO;
    self.navigationController.navigationBar.translucent = NO;
    self.tabBarController.tabBar.translucent = NO;
#endif
    
    curRow = NSNotFound;
   
    NSString *path=[[NSBundle mainBundle] pathForResource:@"citydict" ofType:@"plist"];
    self.cities = [[NSMutableDictionary alloc] initWithContentsOfFile:path];

    self.keys = [NSMutableArray arrayWithArray:[[cities allKeys] sortedArrayUsingSelector:@selector(compare:)]];
}

-(void)locationService
{
    if ([CLLocationManager locationServicesEnabled] &&([CLLocationManager authorizationStatus] == kCLAuthorizationStatusAuthorized
                                                       || [CLLocationManager authorizationStatus] == kCLAuthorizationStatusNotDetermined))
    {
        //定位功能可用，开始定位
        locationManager = [[CLLocationManager alloc] init];
        locationManager.delegate = self;
        locationManager.desiredAccuracy = kCLLocationAccuracyBest;
        locationManager.distanceFilter = 3000.0f;
        [locationManager startUpdatingLocation];
    }else if ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusDenied){
        lable.text = @"请在系统设置中开启定位服务";
        button.enabled = YES;
        lable.userInteractionEnabled=NO;
    }
}

-(void)onLocationLable:(UITapGestureRecognizer *)sender
{
    if(nil != loctionCityCode &&[loctionCityCode isEqualToString:@""]){
        [delegate citySelectionUpdate:loctionCity withCode:loctionCityCode];
        [self performSelector:@selector(removeView)];
    }else{
        UIAlertView *myalert = [[UIAlertView alloc] initWithTitle:@"提示" message:@"暂不支持该城市,清选择其他城市" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil,nil];
		[myalert show];
    }
}

- (void)reLoadLoction:(id)sender
{
    button.enabled = NO;
    lable.userInteractionEnabled=NO;
    lable.text = @"正在定位...";
    [self locationService];
}

- (void)clickToHome:(id)sender
{
    [self removeView];
}

- (void)removeView
{
    CATransition *transition = [CATransition animation];
    transition.duration =0.3f;
    transition.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseInEaseOut];
    transition.type = kCATransitionReveal;
    transition.subtype = kCATransitionFromBottom;
    transition.delegate = self;
    [self.navigationController.view.layer addAnimation:transition forKey:nil];
    
    self.navigationController.navigationBarHidden = NO;
    [self.navigationController popViewControllerAnimated:NO];
}
#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    //搜索出来只显示一块
    if (self.isSearch) {
        return 1;
    }
    return [keys count];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (self.isSearch) {
        return self.tempArray.count;
    }
    NSString *key = [keys objectAtIndex:section];
    NSArray *citySection = [cities objectForKey:key];
    return [citySection count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"cityCell";
    NSString *key = [keys objectAtIndex:indexPath.section];
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        cell.selectionStyle =UITableViewCellSelectionStyleNone;
        cell.textLabel.font = [UIFont systemFontOfSize:18];
    }
    if (self.isSearch) {
        cell.textLabel.text = [self.tempArray objectAtIndex:indexPath.row];
    }else{
        cell.textLabel.text = [[[cities objectForKey:key] objectAtIndex:indexPath.row] objectForKey:@"name"];
    }
    cell.accessoryType = UITableViewCellAccessoryNone;
    
    return cell;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    if (self.isSearch) {
        return nil;
    }
    NSString *key = [keys objectAtIndex:section];
    return key;
}

- (NSArray *)sectionIndexTitlesForTableView:(UITableView *)tableView
{
    return keys;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    curRow = indexPath.row;
    curSection = indexPath.section;
    if (self.isSearch){
        [delegate citySelectionUpdate:tempArray[curRow] withCode:codeArray[curRow]];
    }else{
        if (curRow != NSNotFound) {
            NSString* key = [keys objectAtIndex:curSection];
            [delegate citySelectionUpdate:[[[cities objectForKey:key] objectAtIndex:curRow]  objectForKey:@"name"] withCode:[[[cities objectForKey:key] objectAtIndex:curRow]  objectForKey:@"cityCode"]];
        }
    }
    
    [self performSelector:@selector(removeView)];
}

- (void)searchBarTextDidEndEditing:(UISearchBar *)searchBar
{
    self.isSearch = NO;
}

-(void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText
{
    if (searchText.length == 0) {
        self.isSearch = NO;
    }else{
        self.isSearch = YES;
        [self findContext];
    }
}

-(void)findContext
{
    [tempArray removeAllObjects];
    [codeArray removeAllObjects];
    int i,j;
    NSString *key;
    NSArray *value;
    int count = [keys count];
    for (i = 0; i < count; i++)
    {
        key = [keys objectAtIndex: i];
        value = [cities objectForKey: key];
        if ([key hasPrefix:_searchBar.text])
        {
            for (j=0; j<[value count]; j++) {
                [tempArray addObject:[[value objectAtIndex:j] objectForKey:@"name"]];
                [codeArray addObject:[[value objectAtIndex:j] objectForKey:@"cityCode"]];
            }
            break;
        }else{
            for (j=0; j<[value count]; j++) {
                if ([[[value objectAtIndex:j] objectForKey:@"name"] hasPrefix:_searchBar.text]) {
                    [tempArray addObject:[[value objectAtIndex:j] objectForKey:@"name"]];
                    [codeArray addObject:[[value objectAtIndex:j] objectForKey:@"cityCode"]];
                }
            }
        }
    }
    [tbView reloadData];
}

- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations
{
    CLLocation *currLocation = [locations lastObject];
    CLGeocoder *geocoder = [[CLGeocoder alloc] init];
    CLLocationCoordinate2D myCoOrdinate;
    myCoOrdinate.latitude = currLocation.coordinate.latitude;
    myCoOrdinate.longitude = currLocation.coordinate.longitude;
    
    CLLocation *location = [[CLLocation alloc] initWithLatitude:myCoOrdinate.latitude longitude:myCoOrdinate.longitude];
    [geocoder reverseGeocodeLocation:location completionHandler:^(NSArray *placemarks, NSError *error)
     {
         if (error)
         {
             lable.text = @"无法成功定位";
             button.enabled = YES;
             lable.userInteractionEnabled=NO;
             return;
         }
         if(placemarks.count > 0)
         {
             
             NSString *city = @"";
             CLPlacemark *placemark = placemarks[0];
             if([placemark.addressDictionary objectForKey:@"City"] != NULL)
             {
                 city = [placemark.addressDictionary objectForKey:@"City"];
                 lable.text = [NSString stringWithFormat:@"%@%@",@"定位城市:",city];
                 button.enabled = YES;
                 lable.userInteractionEnabled=YES;
                 loctionCity = city;
                 NSArray * value;
                 int i ;
                 int j ;
                 int count = [keys count];
                 for (i = 0; i < count; i++)
                 {
                     value = [cities objectForKey: [keys objectAtIndex: i]];
                     for (j=0; j<[value count]; j++) {
                         if ([[[value objectAtIndex:j] objectForKey:@"name"] isEqualToString:city]) {
                             loctionCityCode = [[value objectAtIndex:j] objectForKey:@"cityCode"];
                             break;
                         }
                    }
                 }
                 
                 NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
                 [defaults setObject:@"1" forKey:@"isLocation"];
                 [defaults setObject:city forKey:@"currentCity"];
                 [defaults setObject:[NSString stringWithFormat:@"%lf",myCoOrdinate.latitude] forKey:@"latitude"];
                 [defaults setObject:[NSString stringWithFormat:@"%lf",myCoOrdinate.longitude] forKey:@"longitude"];
                 [defaults setObject:loctionCityCode forKey:@"cityCode"];
                 [defaults synchronize];
             }else if(placemark.administrativeArea){
                 city = [placemark.administrativeArea substringToIndex:2];
                 lable.text = [NSString stringWithFormat:@"%@%@",@"定位城市:",city];
                 button.enabled = YES;
                 lable.userInteractionEnabled=YES;
                 loctionCity = city;
                 NSArray * value;
                 int i ;
                 int j ;
                 int count = [keys count];
                 for (i = 0; i < count; i++)
                 {
                     value = [cities objectForKey: [keys objectAtIndex: i]];
                     for (j=0; j<[value count]; j++) {
                         if ([[[value objectAtIndex:j] objectForKey:@"name"] isEqualToString:city]) {
                             loctionCityCode = [[value objectAtIndex:j] objectForKey:@"cityCode"];
                             break;
                         }
                     }
                 }
                 
                 NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
                 [defaults setObject:@"1" forKey:@"isLocation"];
                 [defaults setObject:city forKey:@"currentCity"];
                 [defaults setObject:loctionCityCode forKey:@"cityCode"];
                 [defaults setObject:[NSString stringWithFormat:@"%lf",myCoOrdinate.latitude] forKey:@"latitude"];
                 [defaults setObject:[NSString stringWithFormat:@"%lf",myCoOrdinate.longitude] forKey:@"longitude"];
                 [defaults synchronize];
             }else{
                 lable.text = @"无法成功定位";
                 lable.userInteractionEnabled=NO;
             }
         }
     }];
    [locationManager stopUpdatingLocation];
}
- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error
{
    NSString *errorString;
    [manager stopUpdatingLocation];
    switch([error code]) {
        case kCLErrorDenied:
            //Access denied by user
            errorString = @"无法成功定位";
            //Do something...
            break;
        case kCLErrorLocationUnknown:
            //Probably temporary...
            errorString = @"定位服务不可用";
            //Do something else...
            break;
        default:
            errorString = @"无法成功定位";
            break;
    }
    lable.userInteractionEnabled=NO;
    lable.text = errorString;
    button.enabled = YES;
    return;
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    //停止定位
    [locationManager stopUpdatingLocation];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}
@end
