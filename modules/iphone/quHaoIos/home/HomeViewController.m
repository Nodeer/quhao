//
//  HomeViewController.m
//  quHaoIos
//
//  Created by sam on 13-10-5.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "HomeViewController.h"

@implementation HomeViewController
@synthesize menuView = _menuView;
@synthesize locationManager;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.tabBarItem.title = @"主页";
        self.tabBarItem.image = [UIImage imageNamed:@"home"];
        [self loadNavigationItem];
    }
    return self;
}

-(void)loadNavigationItem
{
    _cityButton = [[UIButton alloc] initWithFrame:CGRectMake( 0, 0, 40, 25 )];
    _cityButton.titleLabel.font = [UIFont boldSystemFontOfSize:15.0f];
    [_cityButton setTitleColor:UIColorFromRGB(0xcb3f40) forState:UIControlStateNormal];
    [_cityButton setTitle: @"上海" forState: UIControlStateNormal];
    [_cityButton addTarget:self action:@selector(openCitySearchView:) forControlEvents:UIControlEventTouchUpInside];
    _cityButton.contentHorizontalAlignment = UIControlContentHorizontalAlignmentRight;
    [_cityButton setContentHorizontalAlignment:UIControlContentHorizontalAlignmentRight];
    UIBarButtonItem *cityButtonItem = [[UIBarButtonItem alloc] initWithCustomView:_cityButton];
    _cityCode = @"021";
    if ([Helper returnUserString:@"currentCity"]!=nil&&[Helper returnUserString:@"currentcityCode"]!=nil)
    {
        [_cityButton  setTitle:[Helper returnUserString:@"currentCity"] forState:UIControlStateNormal];
        _cityCode = [Helper returnUserString:@"currentcityCode"];
    }else{
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        [defaults setObject:_cityCode forKey:@"currentcityCode"];
        [defaults setObject:@"上海" forKey:@"currentCity"];
        [defaults synchronize];
    }
    if(_cityCode == nil){
        _cityCode = @"021";
    }
    UIButton *chooseButton=[Helper getBackBtn:@"chooseArrow" title:@"" rect:CGRectMake( 0, 0, 16, 10 )];
    [chooseButton addTarget:self action:@selector(openCitySearchView:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *chooseButtonItem = [[UIBarButtonItem alloc] initWithCustomView:chooseButton];
    UIBarButtonItem *spaceButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:nil action:nil];
    spaceButton.width = -15;
    NSArray *leftBarButtons = [NSArray arrayWithObjects:spaceButton,cityButtonItem,chooseButtonItem, nil];
    self.navigationItem.leftBarButtonItem = nil;
    self.navigationItem.leftBarButtonItems = leftBarButtons;
    
    //添加搜索的按钮
    UIButton *btn = [[UIButton alloc]initWithFrame:CGRectMake(0, 0, 30, 30)];
    [btn addTarget:self action:@selector(clickSearch:) forControlEvents:UIControlEventTouchUpInside];
    [btn setImage:[UIImage imageNamed:@"search"] forState:UIControlStateNormal];
    UIBarButtonItem *btnSearch = [[UIBarButtonItem alloc]initWithCustomView:btn];
    self.navigationItem.rightBarButtonItem = btnSearch;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    _isLoading = YES;
    _menuView=[[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, kDeviceWidth, kDeviceHeight-108)];
    _menuView.backgroundColor = [UIColor whiteColor];
    self.view = _menuView;
    if (_menuView) {
        _menuView.scrollEnabled = YES;
        _menuView.userInteractionEnabled = YES;
        //_menuView.contentSize = _menuView.frame.size;
        _menuView.showsVerticalScrollIndicator = NO;
    }
    
    self.view.backgroundColor=[UIColor whiteColor];
    //适配iOS7uinavigationbar遮挡tableView的问题
#if IOS7_SDK_AVAILABLE
    self.edgesForExtendedLayout = UIRectEdgeNone;
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.navigationController.navigationBar.translucent = NO;
    self.tabBarController.tabBar.translucent = NO;
    self.extendedLayoutIncludesOpaqueBars = NO;
#endif
    _topIdArray= [[NSMutableArray alloc] init];
    _topUrlArray= [[NSMutableArray alloc] init];
    _activityArray = [[NSMutableArray alloc] init];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshed:) name:Notification_TabClick object:nil];
    
    locationManager = [[CLLocationManager alloc] init];
    locationManager.delegate = self;
    locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    locationManager.distanceFilter = 3000.0f;
    
    if(![Helper isConnectionAvailable]){
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
        return;
    }
    [self locationService];
    
    [self createHud];
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_async(group, queue, ^{
        [self requestTopData];
    });
    dispatch_group_async(group, queue, ^{
        [self requestMenuData];
    });
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        if([_topIdArray count]!=0){
            [self topSetOrReset];
        }
        [self createMiddleView];
        [self menuSetOrReset];
        [self.view bringSubviewToFront:_HUD];
        [_HUD hide:YES];
    });
}

-(void)viewDidAppear:(BOOL)animated
{
    if(_scroller!=nil){
        [_scroller.animationTimer resumeTimerAfterTimeInterval:3];
    }
    if([Helper isConnectionAvailable]){
        [locationManager startUpdatingLocation];
    }
}

-(void)locationService
{
    if ([CLLocationManager locationServicesEnabled] &&([CLLocationManager authorizationStatus] == kCLAuthorizationStatusAuthorized
                                                       || [CLLocationManager authorizationStatus] == kCLAuthorizationStatusNotDetermined))
    {
        //定位功能可用，开始定位
        [locationManager startUpdatingLocation];
    }else if ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusDenied){
        [locationManager stopUpdatingLocation];
        [Helper saveDafaultData:@"0" withName:@"isLocation"];
        
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请在系统设置中开启定位服务" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
    }
}

-(void)openCitySearchView:(id)sender{
    CATransition *animation = [CATransition animation];
    [animation setDuration:0.5];
    [animation setType: kCATransitionMoveIn];
    [animation setSubtype: kCATransitionFromTop];
    [animation setTimingFunction:[CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionDefault]];
    
    CityViewController *city = [[CityViewController alloc] init];
    city.modalTransitionStyle = UIModalTransitionStyleFlipHorizontal;
    city.delegate = self;
    city.hidesBottomBarWhenPushed=YES;
    [self.navigationController pushViewController:city animated:NO];
    [self.navigationController.view.layer addAnimation:animation forKey:nil];
}

- (void)refreshed:(NSNotification *)notification
{
    if (notification.object) {
        if ([(NSString *)notification.object isEqualToString:@"0"]) {
            if(_isLoading){
                _isLoading = NO;
                //if([[Helper returnUserString:@"isLocation"] isEqualToString:@"0"]){
                //    [self locationService];
                //}
                [self realRefresh];
            }
        }
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

- (void)realRefresh
{
    if([Helper isConnectionAvailable]){
        [self createHud];
        [_scroller.animationTimer invalidate];
        [_activityArray removeAllObjects];
        [_topIdArray removeAllObjects];
        [_topUrlArray removeAllObjects];
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
        dispatch_group_t group = dispatch_group_create();
        dispatch_group_async(group, queue, ^{
            [self requestTopData];
        });
        dispatch_group_async(group, queue, ^{
            [self requestMenuData];
        });
        dispatch_group_notify(group, dispatch_get_main_queue(), ^{
            for(UIView *view1 in [self.view subviews])
            {
                [view1 removeFromSuperview];
            }
            if([_topIdArray count]!=0){
                [self topSetOrReset];
                [self createMiddleView];
            }
            [self menuSetOrReset];
            if(_HUD!=nil){
                [_HUD hide:YES];
            }
            _isLoading = YES;
        });
    }else{
        _isLoading = YES;
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

//-(void)createMiddleView
//{
//    UICollectionViewFlowLayout *layout = [[UICollectionViewFlowLayout alloc] init];
//    layout.itemSize = CGSizeMake(120, 50);
//    layout.sectionInset = UIEdgeInsetsMake(0, 0, 0, 0);
//    layout.minimumInteritemSpacing = 10;
//
//    UICollectionView *myCollectionView=[[UICollectionView alloc] initWithFrame:CGRectMake(10, 125, kDeviceWidth-20, 45) collectionViewLayout:layout];
//    [myCollectionView registerClass:[UICollectionViewCell class] forCellWithReuseIdentifier:@"collection"];
//    myCollectionView.backgroundColor=[UIColor whiteColor];
//    myCollectionView.delegate=self;
//    myCollectionView.dataSource=self;
//    [self.view addSubview:myCollectionView];
//    _middleBtn = @[@"middle1.jpg",@"middle2.jpg"];
//
//}
//
//#pragma mark - collection数据源代理
//- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
//{
//    return _middleBtn.count;
//}
//
//- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
//{
//    UICollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"collection" forIndexPath:indexPath];
//    cell.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:_middleBtn[indexPath.row]]];
//
//    return cell;
//}
//
//- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
//{
//   // if(indexPath.row)
//}

-(void)createMiddleView
{
    
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    btn.frame = CGRectMake(10 , 115, 145  , 55);
    btn.backgroundColor =  [UIColor clearColor];
    [btn setBackgroundImage:[UIImage imageNamed:@"qhpd"] forState:UIControlStateNormal];
    [btn addTarget:self action:@selector(qhpd:) forControlEvents:UIControlEventTouchDown];
    [self.view addSubview:btn];
    
    UIButton *zbbpdbtn = [UIButton buttonWithType:UIButtonTypeCustom];
    zbbpdbtn.frame = CGRectMake(kDeviceWidth-155 , 115, 145  , 55);
    zbbpdbtn.backgroundColor =  [UIColor clearColor];
    [zbbpdbtn setBackgroundImage:[UIImage imageNamed:@"msjc"] forState:UIControlStateNormal];
    [zbbpdbtn addTarget:self action:@selector(zbpd:) forControlEvents:UIControlEventTouchDown];
    [self.view addSubview:zbbpdbtn];
    
    UIButton *qhpdBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    qhpdBtn.frame = CGRectMake(10 , 178, 145  , 55);
    qhpdBtn.backgroundColor =  [UIColor clearColor];
    [qhpdBtn setBackgroundImage:[UIImage imageNamed:@"llt"] forState:UIControlStateNormal];
    [qhpdBtn addTarget:self action:@selector(zbpd:) forControlEvents:UIControlEventTouchDown];
    [self.view addSubview:qhpdBtn];
    
    UIButton *wdgzdbtn = [UIButton buttonWithType:UIButtonTypeCustom];
    wdgzdbtn.frame = CGRectMake(kDeviceWidth-155 , 178, 145  , 55);
    wdgzdbtn.backgroundColor =  [UIColor clearColor];
    [wdgzdbtn setBackgroundImage:[UIImage imageNamed:@"wdgz"] forState:UIControlStateNormal];
    [wdgzdbtn addTarget:self action:@selector(wdgz:) forControlEvents:UIControlEventTouchDown];
    [self.view addSubview:wdgzdbtn];
    
    UIImageView *imageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"xzkn"]];
    imageView.frame = CGRectMake(10 , wdgzdbtn.frame.origin.y+wdgzdbtn.frame.size.height+8, kDeviceWidth-20  , 78);
    UITapGestureRecognizer *Tap =[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(xzkn:)];
    [Tap setNumberOfTapsRequired:1];
    [Tap setNumberOfTouchesRequired:1];
    imageView.userInteractionEnabled=YES;
    [imageView addGestureRecognizer:Tap];
    [self.view addSubview:imageView];
}

-(void)xzkn:(id)sender
{
    if([Helper isConnectionAvailable]){
        NSString *url = [NSString stringWithFormat:@"%@%@?cityCode=%@",IP,getTuiJian_url,_cityCode];
        NSString *response1 = [QuHaoUtil requestDb:url];
        if([response1 isEqualToString:@""]){
            [Helper showHUD2:@"网络异常,请稍后再试" andView:self.view andSize:100];
            return;
        }else{
            NSDictionary *jsonObjects=[QuHaoUtil analyseDataToDic:response1];
            if(jsonObjects==nil){
                //解析错误
                [Helper showHUD2:@"网络异常,请稍后再试" andView:self.view andSize:100];
                return;
            }else{
                MerchartDetail *mDetail = [[MerchartDetail alloc] init];
                mDetail.merchartID = [jsonObjects  objectForKey:@"id"];
                mDetail.distance = [jsonObjects objectForKey:@"distance"];
                mDetail.hidesBottomBarWhenPushed=YES;
                [self.navigationController pushViewController:mDetail animated:YES];
            }
        }
    }else{
        _isLoading = YES;
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

-(void)qhpd:(id)sender
{
    ListViewController *home = [[ListViewController alloc] init];
    home.cateType = @"";
    home.cityCode = _cityCode;
    home.latitude = [[Helper returnUserString:@"latitude"] doubleValue];
    home.longitude = [[Helper returnUserString:@"longitude"] doubleValue];
    home.hidesBottomBarWhenPushed=YES;
    [self.navigationController pushViewController:home animated:YES];
}

-(void)wdgz:(id)sender
{
    Helper *helper=[Helper new];
    if (![Helper isCookie]) {
        LoginView *loginView = [[LoginView alloc] init];
        loginView._isPopupByNotice = YES;
        helper.viewBeforeLogin = self;
        helper.viewNameBeforeLogin = @"HomeViewController";
        loginView.helper=helper;
        loginView.hidesBottomBarWhenPushed=YES;
        [self.navigationController pushViewController:loginView animated:YES];
        return;
    }else{
        AttentionViewController *att = [[AttentionViewController alloc] init];
        att.accountId = [Helper getUID];
        att.latitude = [[Helper returnUserString:@"latitude"] doubleValue];
        att.longitude = [[Helper returnUserString:@"longitude"] doubleValue];
        att.hidesBottomBarWhenPushed=YES;
        [self.navigationController pushViewController:att animated:YES];
    }
}

-(void)zbpd:(id)sender
{
    NoQueueViewController *att = [[NoQueueViewController alloc] init];
    att.hidesBottomBarWhenPushed=YES;
    [self.navigationController pushViewController:att animated:YES];
}

-(void)requestTopData
{
    //处理topMerchant
    NSString *url = [NSString stringWithFormat:@"%@%@%d&cityCode=%@",IP,getTopMerchants,topSize,_cityCode];
    NSString *response1 =[QuHaoUtil requestDb:url];
    [_topUrlArray removeAllObjects];
    [_topIdArray removeAllObjects];
    if([response1 isEqualToString:@""]){
        //异常处理
        _HUD.labelText = @"网路异常,请稍后再试";
        [_HUD hide:YES afterDelay:1];
        for(int j=0; j < 6;j++ ){
            [_topIdArray addObject:@""];
            [_topUrlArray addObject:@""];
        }
    }else{
        NSArray *jsonObjects=[QuHaoUtil analyseData:response1];
        if(jsonObjects==nil){
            //解析错误
            _HUD.labelText = @"网路异常,请稍后再试";
            [_HUD hide:YES afterDelay:1];
            for(int j=0; j < 6;j++ ){
                [_topIdArray addObject:@""];
                [_topUrlArray addObject:@""];
            }
            return;
        }else{
            for(int i=0; i < [jsonObjects count];i++ ){
                [_topUrlArray addObject:[[jsonObjects objectAtIndex:i] objectForKey:@"merchantImage"]];
                [_topIdArray addObject:[[jsonObjects objectAtIndex:i] objectForKey:@"mid"]];
            }
            for(int j=(int)[jsonObjects count]; j < 6;j++ ){
                [_topIdArray addObject:@""];
                [_topUrlArray addObject:@""];
            }
            //            if([jsonObjects count] == 0){
            //                [_topIdArray addObject:@""];
            //                [_topUrlArray addObject:@""];
            //            }
        }
    }
}

-(void)requestMenuData
{
    //加载category
    NSString *url = [NSString stringWithFormat:@"%@%@?cityCode=%@&userX=%f&userY=%f",IP,getActivity_url,_cityCode,_latitude,_longitude];
    NSString *response1 = [QuHaoUtil requestDb:url];
    if([response1 isEqualToString:@""]){
        //异常处理
        //[Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        return;
    }else{
        NSArray *jsonObjects=[QuHaoUtil analyseData:response1];
        if(jsonObjects==nil){
            //解析错误
            //[Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            return;
        }else{
            [_activityArray removeAllObjects];
            Activity *c = nil;
            for(int i=0; i < [jsonObjects count]; ){
                c = [[Activity alloc] init];
                c.mid = [[jsonObjects objectAtIndex:i] objectForKey:@"mid"];
                c.image = [[jsonObjects objectAtIndex:i] objectForKey:@"image"];
                [_activityArray insertObject:c atIndex:i];
                
                i++;
            }
        }
    }
}

-(void)menuSetOrReset {
    if([_activityArray count] != 0){
        UIImageView *hd = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"yhhd"]];
        hd.frame = CGRectMake(0 , 320, kDeviceWidth  , 38);
        [self.view addSubview:hd];
        
        float height = 360;
        Activity *ac = nil;
        for(int i=0;i<[_activityArray count];i++){
            ac = [_activityArray objectAtIndex:i];
            EGOImageView * imgView = [[EGOImageView alloc] init];
            imgView.imageURL =[NSURL URLWithString:[NSString stringWithFormat:@"%@%@",IP,[ac.image    stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding ]]];
            [imgView setFrame:CGRectMake(10, height ,kDeviceWidth -20 , 100)];
            imgView.tag = i;
            imgView.id = ac.mid;
            UITapGestureRecognizer *Tap =[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onClickUIImage:)];
            [Tap setNumberOfTapsRequired:1];
            [Tap setNumberOfTouchesRequired:1];
            imgView.userInteractionEnabled=YES;
            [imgView addGestureRecognizer:Tap];
            [self.view addSubview:imgView];
            height += 110;
        }
        _menuView.contentSize = CGSizeMake(kDeviceWidth, height);
    }
}

//搜索的点击事件
- (void)clickSearch:(id)sender
{
    SearchView * sView = [[SearchView alloc] init];
    sView.cityCode = _cityCode;
    sView.hidesBottomBarWhenPushed = YES;
    [self.navigationController pushViewController:sView animated:YES];
}

-(void)topSetOrReset
{
    _scroller=[[EScrollerView alloc] initWithFrameRect:CGRectMake(0, 0, kDeviceWidth, 105)
                                            ImageArray:_topUrlArray
                                            TitleArray:_topIdArray];
    _scroller.delegate=self;
    [self.view addSubview:_scroller];
}

-(void)EScrollerViewDidClicked:(NSUInteger)index
{
    [self onClickTopImage:_topIdArray[(int)index-1]];
}

-(void)onClickUIImage:(UITapGestureRecognizer *)sender
{
    UITapGestureRecognizer *tap = (UITapGestureRecognizer*)sender;
    EGOImageView *image=(EGOImageView*)tap.view;
    MerchartDetail *mDetail = [[MerchartDetail alloc] init];
    mDetail.merchartID = image.id;
    mDetail.tabBarItem.image = [UIImage imageNamed:@"detail"];
    mDetail.hidesBottomBarWhenPushed=YES;
    [self.navigationController pushViewController:mDetail animated:YES];
}

-(void)onClickTopImage:(NSString *)mid
{
    if(NULL == mid || [mid isEqualToString:@""]){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"推荐商家虚席以待" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    
    MerchartDetail *mDetail = [[MerchartDetail alloc] init];
    mDetail.merchartID = mid;
    mDetail.tabBarItem.image = [UIImage imageNamed:@"detail"];
    mDetail.hidesBottomBarWhenPushed=YES;
    [self.navigationController pushViewController:mDetail animated:YES];
}

-(void)onClickCateLable:(UITapGestureRecognizer *)sender
{
    UITapGestureRecognizer *tap = (UITapGestureRecognizer*)sender;
    UICustomLabel *la=(UICustomLabel*)tap.view;
    [self pushMerchartDetail:la.cateType andNavController:self.navigationController];
}

//弹出商家列表页面
- (void)pushMerchartDetail:(NSString *)cateType andNavController:(UINavigationController *)navController
{
    ListViewController *home = [[ListViewController alloc] init];
    home.cateType = cateType;
    home.cityCode = _cityCode;
    home.latitude = [[Helper returnUserString:@"latitude"] doubleValue];
    home.longitude = [[Helper returnUserString:@"longitude"] doubleValue];
    home.hidesBottomBarWhenPushed=YES;
    [navController pushViewController:home animated:YES];
}

//CityViewController delegate
- (void) citySelectionUpdate:(NSString*)selectedCity withCode:(NSString *)code
{
    [_cityButton setTitle:selectedCity forState:UIControlStateNormal];
    _cityCode = code;
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:selectedCity forKey:@"currentCity"];
    [defaults setObject:_cityCode forKey:@"currentcityCode"];
    [defaults synchronize];
    
    [self realRefresh];
}

- (NSString*) getDefaultCity
{
    return _cityButton.titleLabel.text;
}

- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations
{
    CLLocation *currLocation = [locations lastObject];
    //    NSTimeInterval howRecent = [currLocation.timestamp timeIntervalSinceNow];
    //    if(howRecent < -10 || currLocation.horizontalAccuracy > 100) {
    //        return;
    //    }
    CLGeocoder *geocoder = [[CLGeocoder alloc] init];
    CLLocationCoordinate2D myCoOrdinate;
    if (![WGS84TOGCJ02 isLocationOutOfChina:[currLocation coordinate]]) {
        //转换后的coord
        CLLocationCoordinate2D coord = [WGS84TOGCJ02 transformFromWGSToGCJ:[currLocation coordinate]];
        myCoOrdinate.latitude = coord.latitude;
        myCoOrdinate.longitude = coord.longitude;
    }else{
        myCoOrdinate.latitude = currLocation.coordinate.latitude;
        myCoOrdinate.longitude = currLocation.coordinate.longitude;
    }
    _longitude = myCoOrdinate.longitude;
    _latitude = myCoOrdinate.latitude;
    [locationManager stopUpdatingLocation];
    CLLocation *location = [[CLLocation alloc] initWithLatitude:_latitude longitude:_longitude];
    [geocoder reverseGeocodeLocation:location completionHandler:^(NSArray *placemarks, NSError *error)
     {
         if (error)
         {
             [Helper saveDafaultData:@"0" withName:@"isLocation"];
             return;
         }
         if(placemarks.count > 0)
         {
             
             NSString *city = @"";
             NSString *isLocation = [Helper returnUserString:@"isLocation"];
             NSString *tempCityCode = @"";
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
                             tempCityCode = [[value objectAtIndex:j] objectForKey:@"cityCode"];
                             break;
                         }
                     }
                 }
                 if(![isLocation isEqualToString:@"1"]){
                     [_cityButton setTitle:city forState:UIControlStateNormal];
                     _cityCode = tempCityCode;
                 }
                 NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
                 [defaults setObject:@"1" forKey:@"isLocation"];
                 [defaults setObject:city forKey:@"locationCity"];
                 [defaults setObject:[NSString stringWithFormat:@"%lf",_latitude] forKey:@"latitude"];
                 [defaults setObject:[NSString stringWithFormat:@"%lf",_longitude] forKey:@"longitude"];
                 [defaults setObject:tempCityCode forKey:@"cityCode"];
                 if(![isLocation isEqualToString:@"1"]){
                     [defaults setObject:city forKey:@"currentCity"];
                     [defaults setObject:tempCityCode forKey:@"currentcityCode"];
                     [self realRefresh];
                 }
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
                             tempCityCode = [[value objectAtIndex:j] objectForKey:@"cityCode"];
                             break;
                         }
                     }
                 }
                 if(![isLocation isEqualToString:@"1"]){
                     [_cityButton setTitle:city forState:UIControlStateNormal];
                     _cityCode = tempCityCode;
                     [self realRefresh];
                 }
                 NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
                 [defaults setObject:@"1" forKey:@"isLocation"];
                 [defaults setObject:city forKey:@"locationCity"];
                 [defaults setObject:tempCityCode forKey:@"cityCode"];
                 if(![isLocation isEqualToString:@"1"]){
                     [defaults setObject:city forKey:@"currentCity"];
                     [defaults setObject:tempCityCode forKey:@"currentcityCode"];
                 }
                 [defaults setObject:[NSString stringWithFormat:@"%lf",_latitude] forKey:@"latitude"];
                 [defaults setObject:[NSString stringWithFormat:@"%lf",_longitude] forKey:@"longitude"];
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
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: errorString delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
    [alert show];
    return;
}

-(void) viewDidDisappear:(BOOL)animated
{
    [_scroller.animationTimer pauseTimer];
}

- (void)viewDidUnload
{
    _menuView = nil;
    [_activityArray removeAllObjects];
    [_topIdArray removeAllObjects];
    [_topUrlArray removeAllObjects];
    
    [super viewDidUnload];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void)dealloc
{
    [_menuView setDelegate:nil];
}
@end
