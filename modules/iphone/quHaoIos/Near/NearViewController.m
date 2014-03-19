//
//  NearViewController.m
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "NearViewController.h"
#define SearchKey @"7e5d17f015e4c22936fc3fd7341a903d"

@interface NearViewController ()

@property (nonatomic, strong) MAPointAnnotation *centerPointAnnotation;
//@property (nonatomic, strong) MAPoiSearchOption *poiSearchOption;
@property (nonatomic) AMapSearchType searchType;

@end

@implementation NearViewController
@synthesize centerPointAnnotation = _centerPointAnnotation;
//@synthesize poiSearchOption = _poiSearchOption;
@synthesize searchType = _searchType;

- (id)init
{
    self = [super init];
    if (self)
    {
        [MAMapServices sharedServices].apiKey = SearchKey;
        self.searchType = AMapSearchType_PlaceAround;
        self.title=@"周边美食";
        self.tabBarItem.image = [UIImage imageNamed:@"near"];
        _isOpinion = NO;
        [self initMapView];
    }
    
    return self;
}

//- (void)initMapView
//{
//    self.ownMapView = [[MAMapView alloc] initWithFrame:self.view.bounds];
//    self.ownMapView.showsUserLocation = YES;
//    //self.search = [[MASearch alloc] initWithSearchKey:SearchKey Delegate:self];
//}

- (void)initMapView
{
    self.ownMapView = [[MAMapView alloc] initWithFrame:self.view.bounds];
    self.ownMapView.showsUserLocation = YES;
    
    
    self.ownMapView.visibleMapRect = MAMapRectMake(220880104, 101476980, 272496, 466656);
    
    self.search = [[AMapSearchAPI alloc] initWithSearchKey:[MAMapServices sharedServices].apiKey Delegate:self];
    self.search.delegate = self;
}

- (void)viewDidAppear:(BOOL)animated
{
    CGSize size=CGSizeMake(500,44);
    [self.navigationController.navigationBar setBackgroundImage:[Helper reSizeImage:@"title.jpg" toSize:size] forBarMetrics:UIBarMetricsDefault];
    
    _merchartsArray = [[NSMutableArray alloc] initWithCapacity:20];
    _reloading = NO;
    _pageIndex=1;
    //注册
    [self.tableView registerClass:[NearCell class] forCellReuseIdentifier:@"NearCell"];
    if([Helper isConnectionAvailable]){
        if([_merchartsArray count]==0){
            

            
            MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
            hud.labelText = NSLocalizedString(@"正在加载", nil);
            hud.square = YES;
            [self refreshAction];

            [hud hide:YES];
            [self addFooter];
        }
    }//如果没有网络连接
    else
    {
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

//上拉加载更多
- (void)addFooter
{
    MJRefreshFooterView *footer = [MJRefreshFooterView footer];
    footer.scrollView = self.tableView;
    footer.beginRefreshingBlock = ^(MJRefreshBaseView *refreshView) {
        _prevItemCount = [_merchartsArray count];
        ++_pageIndex;
        [self refreshAction];
        [self performSelector:@selector(doneWithView:) withObject:refreshView afterDelay:1.0];
        
    };
    _footer = footer;
}

- (void)doneWithView:(MJRefreshBaseView *)refreshView
{
    // 刷新表格
    [self.tableView reloadData];
    // 调用endRefreshing结束刷新状态
    [refreshView endRefreshing];
}

//设置行高
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
    //if ([_merchartsArray count] > 0) {
    self.tableView.separatorStyle =UITableViewCellSeparatorStyleSingleLine;
    static NSString *cellIdentify=@"NearCell";
    NearCell *cell=[tableView dequeueReusableCellWithIdentifier:cellIdentify];
    //检查视图中有没闲置的单元格
    if(cell==nil){
        cell=[[NearCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentify];
    }
    cell.merchartModel=_merchartsArray[indexPath.row];
    [Helper arrowStyle:cell];

    return cell;
}

//选中一条纪录触发的事件
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    int row = [indexPath row];
    if (row >= [_merchartsArray count]) {
        [self performSelector:@selector(reload:)];
    }
    else {
        MerchartModel *n = [_merchartsArray objectAtIndex:row];
        if (n&&n.id!=nil)
        {
            [self pushMerchartDetail:n andNavController:self.navigationController];
        }else{
            //当前只提交数据库一次
            if (!_isOpinion) {
                NSString *url = [NSString stringWithFormat:@"%@%@%@",[Helper getIp],updateGaodeToMerchant,n.pguid];
                NSString *response =[QuHaoUtil requestDb:url];
                if(![response isEqualToString:@""]){
                    [Helper showHUD2:@"暂时未开放！" andView:self.view andSize:100];
                }
                _isOpinion=YES;
            }else{
                
                [Helper showHUD2:@"暂时未开放！" andView:self.view andSize:100];
            }
        }
    }
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


- (void)poiRequestCoordinate:(CLLocationCoordinate2D)coordinate
{
    AMapPlaceSearchRequest *request = [[AMapPlaceSearchRequest alloc] init];
    
    request.searchType = AMapSearchType_PlaceAround;
    
    request.location = [AMapGeoPoint locationWithLatitude:coordinate.latitude longitude:coordinate.longitude];
    request.keywords = @"餐饮";
    request.radius = @"3000";
    request.page = _pageIndex;
    request.sortrule = 1;
    request.offset=20;
    //返回扩展信息
    //request.requireExtension = YES;
    
    [self.search AMapPlaceSearch:request];
//前一版本
//    self.poiSearchOption = [[MAPoiSearchOption alloc] init];
//    
//    self.poiSearchOption.config = @"BELSBXY";
//    self.poiSearchOption.encode = @"UTF-8";
//    self.poiSearchOption.range=@"3000";
//    self.poiSearchOption.sr=@"1";
//    self.poiSearchOption.batch=[NSString stringWithFormat:@"%d",pageIndex];
//    self.poiSearchOption.number=@"10";
//    
//    self.poiSearchOption.cenX   = [NSString stringWithFormat:@"%f", coordinate.longitude];
//    self.poiSearchOption.cenY   = [NSString stringWithFormat:@"%f", coordinate.latitude];
//    
//    [self.search poiSearchWithOption:self.poiSearchOption];
}

#pragma mark - AMapSearchDelegate

/* POI 搜索回调. */
- (void)onPlaceSearchDone:(AMapPlaceSearchRequest *)request response:(AMapPlaceSearchResponse *)respons
{
    if (request.searchType != self.searchType)
    {
        return;
    }
    if (respons.pois.count == 0)
    {
        return;
    }
    
    //NSMutableArray *poiAnnotations = [NSMutableArray arrayWithCapacity:respons.pois.count];
    
    [respons.pois enumerateObjectsUsingBlock:^(AMapPOI *obj, NSUInteger idx, BOOL *stop) {

        //[poiAnnotations addObject:[[POIAnnotation alloc] initWithPOI:obj]];
        MerchartModel *model=[[MerchartModel alloc]init];
        model.name=obj.name;
        if (obj.distance<=1000) {
            model.distance=[NSString stringWithFormat:@"%dm",obj.distance];
        } else {
            float dis=obj.distance/1000;
            model.distance=[NSString stringWithFormat:@"%.1fkm",dis];
        }
        model.pguid=obj.uid;
        model.imgUrl=@"";
        NSString * result=[self getMerchart:obj.uid ];
        if(![result isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            model.id=result;
        }
        [_merchartsArray addObject:model];
        
    }];
    _prevItemCount = [_merchartsArray count];
    
    [self.tableView reloadData];
}

//#pragma mark - POI delegate
//
//-(void)poiSearch:(MAPoiSearchOption *)poiSearchOption Result:(MAPoiSearchResult *)result
//{
//    if (self.poiSearchOption != poiSearchOption)
//    {
//        return;
//    }
//    [result.pois enumerateObjectsUsingBlock:^(MAPOI *poi, NSUInteger idx, BOOL *stop) {
//
//        MerchartModel *model=[[MerchartModel alloc]init];
//        model.name=poi.name;
//        float dis=[poi.distance floatValue]/1000;
//        model.distance=[NSString stringWithFormat:@"%.2fkm",dis];
//        model.pguid=poi.pguid;
//        model.imgUrl=poi.url;
//        NSString * result=[self getMerchart:poi.pguid ];
//        if(![result isEqualToString:@""]){
//            //异常处理
//            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
//            model.id=result;
//        }
//
////        NSLog(@"poi name : %@",poi.name);
////        //NSLog(@"poi address : %@",poi.address);
////        NSLog(@"poi type : %@",poi.type);
////        NSLog(@"poi url : %@",poi.url);
////        NSLog(@"poi citycode : %@",poi.code);
////       // NSLog(@"poi pguid: %@",poi.pguid);
////        //NSLog(@"poi tel: %@",poi.tel);
////        //NSLog(@"poi x: %@",poi.x);
////        //NSLog(@"poi y: %@",poi.y);
////        NSLog(@"poi distance: %@",poi.distance);
////        NSLog(@"poi match: %@",poi.match);
////        NSLog(@"poi code: %@",poi.code);
//        [_merchartsArray addObject:model];
//    }];
//    prevItemCount = [_merchartsArray count];
//
//    [self.tableView reloadData];
//}

#pragma mark - Action Handle

- (void)refreshAction
{
    //[self initMapView];
    //测试用
    self.ownMapView.userLocation.coordinate= CLLocationCoordinate2DMake(31.138869,121.40948);
    self.centerPointAnnotation = [[MAPointAnnotation alloc] init];
    self.centerPointAnnotation.coordinate = self.ownMapView.userLocation.coordinate;
    self.centerPointAnnotation.title = @"我的位置";
    [self.ownMapView addAnnotation:self.centerPointAnnotation];
    //NSLog(@"%f",self.centerPointAnnotation.coordinate.latitude);
    //NSLog(@"%f",self.centerPointAnnotation.coordinate.longitude);

    [self poiRequestCoordinate:self.centerPointAnnotation.coordinate];
}

-(NSString *)getMerchart:(NSString *)poiId
{
    NSString *url = [NSString stringWithFormat:@"%@%@%@",[Helper getIp],queryMerchantByPoiId,poiId];
    NSString *response =[QuHaoUtil requestDb:url];
    
    return response;
}

- (void)viewDidDisappear:(BOOL)animated
{
    self.ownMapView.showsUserLocation = NO;
    self.ownMapView.userTrackingMode  = MAUserTrackingModeNone;
       
    [super viewDidDisappear:animated];
}

- (void)dealloc
{
    _merchartsArray=nil;
    _footer=nil;
}
@end

