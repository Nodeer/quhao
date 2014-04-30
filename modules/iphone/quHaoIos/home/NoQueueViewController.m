//
//  NoQueueViewController.m
//  quHaoIos
//
//  Created by sam on 14-4-28.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "NoQueueViewController.h"

@interface NoQueueViewController ()

@end

@implementation NoQueueViewController
@synthesize tableView;
@synthesize locationManager;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title=@"周边不排队";
        _showList = 0;
        _dis = 3;
        self.view.backgroundColor = [UIColor whiteColor];
    }
    return self;
}

- (void)viewDidLoad
{
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 5, 50, 30 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    self.tableView=[[UITableView alloc] initWithFrame:CGRectMake(0, 25, kDeviceWidth, kDeviceHeight-133)];
    self.tableView.backgroundColor = [UIColor whiteColor];
    self.tableView.delegate=self;
    self.tableView.dataSource=self;
    self.tableView.indicatorStyle=UIScrollViewIndicatorStyleWhite;
    [self.view addSubview:self.tableView];
    _merchartsArray = [[NSMutableArray alloc] initWithCapacity:20];
#if IOS7_SDK_AVAILABLE
    if ([self.tableView respondsToSelector:@selector(setSeparatorInset:)]) {
        [self.tableView setSeparatorInset:UIEdgeInsetsZero];
    }
    self.edgesForExtendedLayout = UIRectEdgeNone;
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.navigationController.navigationBar.translucent = NO;
    self.tabBarController.tabBar.translucent = NO;
    self.extendedLayoutIncludesOpaqueBars = NO;
#endif
    UIImage *backImage = [UIImage imageNamed:@"max_btn"];
    _button = [UIButton buttonWithType:UIButtonTypeCustom];
    _button.frame = CGRectMake(0, 0, kDeviceWidth, 25);
    [_button setBackgroundImage:backImage forState:UIControlStateNormal];
    _button.titleLabel.font = [UIFont boldSystemFontOfSize:13.0f];
    [_button setTitle:@"3千米" forState:UIControlStateNormal];
    [_button addTarget:self action:@selector(changeDis:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:_button];
}

-(void)viewDidAppear:(BOOL)animated
{
    [_merchartsArray removeAllObjects];
    _isLoading = NO;
    _allCount = 0;
    _isLoadOver = NO;
    [self locationService];
}

-(void)locationService
{
    if ([CLLocationManager locationServicesEnabled] &&([CLLocationManager authorizationStatus] == kCLAuthorizationStatusAuthorized
                                                       || [CLLocationManager authorizationStatus] == kCLAuthorizationStatusNotDetermined))
    {
        //定位功能可用，开始定位
        [self createHud];
        locationManager = [[CLLocationManager alloc] init];
        locationManager.delegate = self;
        locationManager.desiredAccuracy = kCLLocationAccuracyBest;
        locationManager.distanceFilter = 100.0f;
        [locationManager startUpdatingLocation];
    }else if ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusDenied){
        [locationManager stopUpdatingLocation];
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请在系统设置中开启定位服务" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
    }
}

#pragma mark HUD
- (void)hudWasHidden:(MBProgressHUD *)hud {
    [_HUD removeFromSuperview];
	_HUD = nil;
}

-(void)createHud
{
    _HUD = [[MBProgressHUD alloc] initWithView:self.view];
    [self.view addSubview:_HUD];
    _HUD.mode = MBProgressHUDModeIndeterminate;
    _HUD.labelText = @"正在加载";
    [_HUD show:YES];
    _HUD.delegate = self;
}

-(void)requestData
{
    if ([Helper isConnectionAvailable]){
        if (_isLoadOver) {
            return;
        }
        int pageIndex = _allCount/10+1;
        NSString *str1= [NSString stringWithFormat:@"%@%@?userX=%@&userY=%@&cityCode=%@&page=%d&maxDis=%d", IP,getNoQueueMerchants_url,_longitude ,_latitude ,[Helper returnUserString:@"currentcityCode"] ,pageIndex ,_dis];
        NSString *response =[QuHaoUtil requestDb:str1];
        if([response isEqualToString:@""]){
            //异常处理
            _HUD.labelText = @"服务器错误";
            [_HUD hide:YES];
        }else{
            NSArray *jsonObjects=[QuHaoUtil analyseData:response];
            if(jsonObjects==nil){
                //解析错误
                _HUD.labelText = @"服务器错误";
                [_HUD hide:YES];
            }else{
                NSMutableArray *newMerc = [self addAfterInfo:jsonObjects];
                NSInteger count = [newMerc count];
                _allCount += count;
                if (count < 10)
                {
                    _isLoadOver = YES;
                }
                [_merchartsArray addObjectsFromArray:newMerc];
            }
        }
    }
    else
    {
        _isLoadOver = YES;
        _HUD.labelText = @"当前网络不可用";
        [_HUD hide:YES];
    }
}

//上拉刷新增加数据
-(NSMutableArray *)addAfterInfo:(NSArray *) objects
{
    NSMutableArray *news = [[NSMutableArray alloc] initWithCapacity:10];
    MerchartModel *model = nil;
    for(int i=0; i < [objects count];i++ ){
        model =[[MerchartModel alloc]init];
        model.name=[[objects objectAtIndex:i] objectForKey:@"name"];
        model.averageCost=[[[objects objectAtIndex:i] objectForKey:@"averageCost"] floatValue];
        model.id=[[objects objectAtIndex:i] objectForKey:@"id"];
        model.imgUrl=[[objects objectAtIndex:i] objectForKey:@"merchantImage"];
        model.enable=[[[objects objectAtIndex:i] objectForKey:@"enable"] boolValue];
        double disTemp=[[[objects objectAtIndex:i] objectForKey:@"distance"] doubleValue];
        if (disTemp<=1000) {
            model.distance=[NSString stringWithFormat:@"%.fm",disTemp];
        } else {
            float dis=disTemp/1000;
            model.distance=[NSString stringWithFormat:@"%.1fkm",dis];
        }
        [news addObject:model];
    }
    return news;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

#pragma mark tableviewdelegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 100;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if ([_merchartsArray count]==0) {
        self.tableView.separatorStyle = NO;
    }else{
        self.tableView.separatorStyle = YES;
    }
    return [_merchartsArray count];
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor whiteColor];
}

//dataSource
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentify=@"NoQueueCell";
    NearCell *cell=[self.tableView dequeueReusableCellWithIdentifier:cellIdentify];
    //检查视图中有没闲置的单元格
    if(cell==nil){
        cell=[[NearCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentify];
        self.tableView.separatorStyle =UITableViewCellSeparatorStyleSingleLine;
    }
    cell.merchartModel=_merchartsArray[indexPath.row];
    [Helper arrowStyle:cell];
    
    return cell;
}

//选中一条纪录触发的事件
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSInteger row = [indexPath row];
    MerchartModel *n = [_merchartsArray objectAtIndex:row];
    
    [self pushMerchartDetail:n andNavController:self.navigationController];
}

//弹出商家详细页面
- (void)pushMerchartDetail:(MerchartModel *)model andNavController:(UINavigationController *)navController
{
    MerchartDetail *mDetail = [[MerchartDetail alloc] init];
    mDetail.merchartID = model.id;
    mDetail.tabBarItem.image = [UIImage imageNamed:@"detail"];
    mDetail.hidesBottomBarWhenPushed=YES;
    [navController pushViewController:mDetail animated:YES];
}

#pragma mark 下拉选择代码开始
-(void)showPopUpwithOption:(NSArray*)option
{
    _selectList = [[selectListView alloc] initWithOption:option];
    _selectList.delegate = self;
    [_selectList showInView:self.view animated:YES];
}

- (void)changeDis:(id)sender {
    if (_showList == 1) {//如果下拉框已显示，什么都不做
        return;
    }else {
        [_selectList fadeOut];
        _showList = 1;
        arryList = @[@"1千米",@"3千米",@"5千米",@"10千米",@"全城"];
        arryValueList = @[@"1",@"3",@"5",@"10",@"-1"];
        [self showPopUpwithOption:arryList];
    }
}

- (void)selectListView:(selectListView *)listView didSelectedIndex:(NSInteger)anIndex{
    [_button setTitle:[arryList objectAtIndex:anIndex] forState:UIControlStateNormal];
    _dis = [[arryValueList objectAtIndex:anIndex] intValue];
    _showList = 0;
    [self createHud];
    _HUD.labelText = @"正在刷新";
    if (self.tableView.contentOffset.y == 0) {
        [self performSelector:@selector(refreshNoQueue:) withObject:nil afterDelay:0.5];
    }else{
        [self.tableView setContentOffset:CGPointZero animated:YES];
    }
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch  locationInView:self.view];
    if (point.y >264) {
        [_selectList fadeOut];
        _showList = 0;
    }
}

- (void)refreshNoQueue:(id)sender
{
    [_merchartsArray removeAllObjects];
    _allCount = 0;
    _isLoadOver = NO;
    _tableFooterView = nil;
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [self requestData];
        dispatch_async(dispatch_get_main_queue(), ^{
            if (_tableFooterView == nil) {
                if(_isLoadOver||[_merchartsArray count]==0){
                    self.tableView.tableFooterView = nil;
                    _isLoading = YES;
                }else{
                    [self createFootView];
                    [self setFootState:PullRefreshNormal];
                }
            }
            [self.tableView reloadData];
            if(_HUD != nil){
                [_HUD hide:YES];
            }
        });
    });
}
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:YES];
    if(_HUD!=nil){
        [_HUD hide:YES];
    }
}

#pragma 上提刷新
-(void)createFootView
{
    self.tableView.tableFooterView = nil;
    _tableFooterView = [[UIView alloc] initWithFrame:CGRectMake(0.0f, 0.0f, kDeviceWidth, 40.0f)];
    _loadMoreText = [[UILabel alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 116.0f, 40.0f)];
    [_loadMoreText setCenter:_tableFooterView.center];
    [_loadMoreText setFont:[UIFont fontWithName:@"Helvetica Neue" size:14]];
    [_loadMoreText setText:@" 上拉显示更多 "];
    UITapGestureRecognizer *tapGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(onClickMoreLable:)];
    _loadMoreText.userInteractionEnabled=YES;
    [_loadMoreText addGestureRecognizer:tapGesture];
    [_tableFooterView addSubview:_loadMoreText];
    
    _tableFooterActivityIndicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(75.0f, 10.0f, 20.0f, 20.0f)];
    [_tableFooterActivityIndicator setActivityIndicatorViewStyle:UIActivityIndicatorViewStyleGray];
    [_tableFooterActivityIndicator stopAnimating];
    [_tableFooterView addSubview:_tableFooterActivityIndicator];
    self.tableView.tableFooterView = _tableFooterView;
}

-(void)onClickMoreLable:(id)sender
{
    if(_state == PullRefreshNormal){
        _isLoading = YES;
        [self setFootState:PullRefreshLoading];
        [self loadMore];
    }
}

-(void)loadMore
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [self requestData];
        dispatch_async(dispatch_get_main_queue(), ^{
            if(_isLoadOver){
                self.tableView.tableFooterView = nil;
                _isLoading = YES;
            }
            [self doneLoadingTableViewData];
            [self.tableView reloadData];
        });
    });
}

- (void)doneLoadingTableViewData
{
    [self refreshScrollViewDataSourceDidFinishedLoading:self.tableView];
    _isLoading = NO;
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    if(scrollView.contentOffset.y + (scrollView.frame.size.height) > scrollView.contentSize.height && !_isLoading &&(_state == PullRefreshNormal))
    {
        _isLoading = YES;
		[self setFootState:PullRefreshLoading];
        [self loadMore];
    }
}

//增加footView状态
- (void)setFootState:(RefreshState)aState{
	
	switch (aState) {
		case PullRefreshNormal:
            _loadMoreText.text = @" 上拉显示更多 ";
			[_tableFooterActivityIndicator stopAnimating];
			break;
		case PullRefreshLoading:
            _loadMoreText.text = @" 正在加载,请稍后";
			[_tableFooterActivityIndicator startAnimating];
			break;
		default:
			break;
	}
	_state = aState;
}

- (void)refreshScrollViewDataSourceDidFinishedLoading:(UIScrollView *)scrollView {
	
    //	[UIView beginAnimations:nil context:NULL];
    //	[UIView setAnimationDuration:.3];
    //	[scrollView setContentInset:UIEdgeInsetsMake(0.0f, 0.0f, 0.0f, 0.0f)];
    //	[UIView commitAnimations];
	
	[self setFootState:PullRefreshNormal];
}

#pragma mark location
- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations
{
    CLLocation *currLocation = [locations lastObject];
    CLGeocoder *geocoder = [[CLGeocoder alloc] init];
    CLLocationCoordinate2D myCoOrdinate;
    myCoOrdinate.latitude = currLocation.coordinate.latitude;
    myCoOrdinate.longitude = currLocation.coordinate.longitude;
    _longitude = [NSString stringWithFormat:@"%lf",currLocation.coordinate.longitude];
    _latitude = [NSString stringWithFormat:@"%lf",currLocation.coordinate.latitude];
    [locationManager stopUpdatingLocation];
    CLLocation *location = [[CLLocation alloc] initWithLatitude:myCoOrdinate.latitude longitude:myCoOrdinate.longitude];
    [geocoder reverseGeocodeLocation:location completionHandler:^(NSArray *placemarks, NSError *error)
     {
         if (error)
         {
             [Helper saveDafaultData:@"0" withName:@"isLocation"];
             if(_HUD != nil){
                 [_HUD hide:YES];
             }
             return;
         }
         dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
             [self requestData];
             dispatch_async(dispatch_get_main_queue(), ^{
                 if (_tableFooterView == nil) {
                     if(_isLoadOver||[_merchartsArray count]==0){
                         self.tableView.tableFooterView = nil;
                         _isLoading = YES;
                     }else{
                         [self createFootView];
                         [self setFootState:PullRefreshNormal];
                     }
                 }
                 [self.tableView reloadData];
                 if(_HUD != nil){
                     [_HUD hide:YES];
                 }
             });
         });
         if(placemarks.count > 0)
         {
             
             NSString *city = @"";
             NSString *cityCode = @"";
             CLPlacemark *placemark = placemarks[0];
             NSString *path=[[NSBundle mainBundle] pathForResource:@"citydict" ofType:@"plist"];
             NSMutableDictionary *cities = [[NSMutableDictionary alloc] initWithContentsOfFile:path];
             NSMutableArray *keys = [NSMutableArray arrayWithArray:[[cities allKeys] sortedArrayUsingSelector:@selector(compare:)]];
             if([placemark.addressDictionary objectForKey:@"City"] != NULL)
             {
                 city = [placemark.addressDictionary objectForKey:@"City"];
                 NSArray * value;
                 int i ;
                 int j ;
                 NSInteger count = [keys count];
                 for (i = 0; i < count; i++)
                 {
                     value = [cities objectForKey: [keys objectAtIndex: i]];
                     for (j=0; j<[value count]; j++) {
                         if ([[[value objectAtIndex:j] objectForKey:@"name"] isEqualToString:city]) {
                             cityCode = [[value objectAtIndex:j] objectForKey:@"cityCode"];
                             break;
                         }
                     }
                 }
                 NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
                 [defaults setObject:@"1" forKey:@"isLocation"];
                 [defaults setObject:city forKey:@"locationCity"];
                 [defaults setObject:city forKey:@"currentCity"];
                 [defaults setObject:[NSString stringWithFormat:@"%lf",myCoOrdinate.latitude] forKey:@"latitude"];
                 [defaults setObject:[NSString stringWithFormat:@"%lf",myCoOrdinate.longitude] forKey:@"longitude"];
                 [defaults setObject:cityCode forKey:@"cityCode"];
                 [defaults setObject:cityCode forKey:@"currentcityCode"];
                 [defaults synchronize];
             }else if(placemark.administrativeArea){
                 city = [placemark.administrativeArea substringToIndex:2];
                 NSArray * value;
                 int i ;
                 int j ;
                 NSInteger count = [keys count];
                 for (i = 0; i < count; i++)
                 {
                     value = [cities objectForKey: [keys objectAtIndex: i]];
                     for (j=0; j<[value count]; j++) {
                         if ([[[value objectAtIndex:j] objectForKey:@"name"] isEqualToString:city]) {
                             cityCode = [[value objectAtIndex:j] objectForKey:@"cityCode"];
                             break;
                         }
                     }
                 }
                 NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
                 [defaults setObject:@"1" forKey:@"isLocation"];
                 [defaults setObject:city forKey:@"locationCity"];
                 [defaults setObject:city forKey:@"currentCity"];
                 [defaults setObject:cityCode forKey:@"currentcityCode"];
                 [defaults setObject:cityCode forKey:@"cityCode"];
                 [defaults setObject:[NSString stringWithFormat:@"%lf",myCoOrdinate.latitude] forKey:@"latitude"];
                 [defaults setObject:[NSString stringWithFormat:@"%lf",myCoOrdinate.longitude] forKey:@"longitude"];
                 [defaults synchronize];
             }else{
                 [Helper saveDafaultData:@"0" withName:@"isLocation"];
             }
         }
     }];
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
    [Helper saveDafaultData:@"0" withName:@"isLocation"];
    if(_HUD != nil){
        [_HUD hide:YES];
    }
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: errorString delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
    [alert show];
    return;
}
@end
