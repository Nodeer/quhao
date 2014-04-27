//
//  AttentionViewController.m
//  quHaoIos
//
//  Created by sam on 14-4-21.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "AttentionViewController.h"

@interface AttentionViewController ()

@end

@implementation AttentionViewController
@synthesize accountId;
-(void)viewDidLoad
{
    [super viewDidLoad];
    //添加上面的导航
    [self loadNavigationItem];
    self.title=@"我的关注";
    _merchartsArray = [[NSMutableArray alloc] initWithCapacity:20];
    //注册
    self.tableView.frame = CGRectMake(0, 0, kDeviceWidth, kDeviceHeight);
    [self.tableView registerClass:[HomeCell class] forCellReuseIdentifier:@"attentionCell"];
    
#if IOS7_SDK_AVAILABLE
    if ([self.tableView respondsToSelector:@selector(setSeparatorInset:)]) {
        [self.tableView setSeparatorInset:UIEdgeInsetsZero];
    }
#endif
    
    [self createHud];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [self requestData];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.tableView reloadData];
            [_HUD hide:YES];
        });
    });
}

- (void)hudWasHidden:(MBProgressHUD *)hud
{
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

-(void)loadNavigationItem
{
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 5, 50, 30 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController popToRootViewControllerAnimated:YES];
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
    cell.backgroundColor = [UIColor clearColor];
}

//设置行高
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 100;
}

//dataSource
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentify=@"attentionCell";
    HomeCell *cell=[tableView dequeueReusableCellWithIdentifier:cellIdentify];
    //检查视图中有没闲置的单元格
    if(cell==nil){
        cell=[[HomeCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentify];
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
    MerchartModel *n = [_merchartsArray objectAtIndex:row];
    if (n)
    {
        [self pushMerchartDetail:n andNavController:self.navigationController andIsNextPage:NO];
    }
    
}

//弹出商家详细页面
- (void)pushMerchartDetail:(MerchartModel *)model andNavController:(UINavigationController *)navController andIsNextPage:(BOOL)isNextPage
{
    MerchartDetail *mDetail = [[MerchartDetail alloc] init];
    mDetail.merchartID = model.id;
    mDetail.isNextPage = isNextPage;
    
    [navController pushViewController:mDetail animated:YES];
}


-(void)requestData
{
    if ([Helper isConnectionAvailable]){
        NSString *str1= [NSString stringWithFormat:@"%@%@%@", IP,attentionView_list_url,self.accountId];
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
                [self addAfterInfo:jsonObjects];
            }
        }
    }
    else
    {
        _HUD.labelText = @"当前网络不可用";
        [_HUD hide:YES];
    }
}

//上拉刷新增加数据
-(void)addAfterInfo:(NSArray *) objects
{
    MerchartModel *model = nil;
    for(int i=0; i < [objects count];i++ ){
        model = [[MerchartModel alloc]init];
        model.name=[[objects objectAtIndex:i] objectForKey:@"name"];
        model.averageCost=[[[objects objectAtIndex:i] objectForKey:@"averageCost"] floatValue];
        model.id=[[objects objectAtIndex:i] objectForKey:@"id"];
        model.imgUrl=[[objects objectAtIndex:i] objectForKey:@"merchantImage"];
        [_merchartsArray addObject:model];
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

@end
