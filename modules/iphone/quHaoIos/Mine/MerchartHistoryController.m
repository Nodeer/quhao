//
//  MerchartHistoryController.m
//  quHaoIos
//
//  Created by sam on 13-10-22.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "MerchartHistoryController.h"

@interface MerchartHistoryController ()

@end

@implementation MerchartHistoryController

-(void)loadNavigationItem
{   
    self.tabBarItem.title=@"历史取号情况";
    UIButton *backButton=[Helper getBackBtn:@"back"];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    _backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = _backButtonItem;
    
    UIButton *btnButton = [Helper getBtn:@" 编 辑" rect:CGRectMake( 0, 0, 40, 25 )];
    [btnButton addTarget:self action:@selector(remove:) forControlEvents:UIControlEventTouchUpInside];
    _editItem = [[UIBarButtonItem alloc] initWithCustomView:btnButton];
    self.navigationItem.rightBarButtonItem = _editItem;
}
- (void)clickToHome:(id)sender
{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

- (void)remove:(id)sender
{
    self.tableView.allowsMultipleSelectionDuringEditing = YES;
    [self.tableView setEditing:YES animated:YES];
    [self updateBarButtons];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    //添加上面的导航
    [self loadNavigationItem];
    
    _mutiButton = [Helper getBtn:@"全部删除" rect:CGRectMake( 0, 0, 60, 25 )];
    [_mutiButton addTarget:self action:@selector(multiDeleteClicked:) forControlEvents:UIControlEventTouchUpInside];
    _multiDeleteBarButton = [[UIBarButtonItem alloc] initWithCustomView:_mutiButton];
    
    UIButton *cancelButton = [Helper getBtn:@"取 消" rect:CGRectMake( 0, 0, 40, 25 )];
    [cancelButton addTarget:self action:@selector(cancelButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    _cancelBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:cancelButton];
    
#if IOS7_SDK_AVAILABLE
    if ([self.tableView respondsToSelector:@selector(setSeparatorInset:)]) {
        [self.tableView setSeparatorInset:UIEdgeInsetsZero];
    }
#endif
    
    _reservationArray = [[NSMutableArray alloc] initWithCapacity:20];
     _delArray = [[NSMutableArray alloc] initWithCapacity:0];
}

-(void)viewDidAppear:(BOOL)animated
{
    [self createHud];
    [_reservationArray removeAllObjects];

    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            [self requestData:[NSString stringWithFormat:@"%@%@%@",IP,history_list_url,self.accouId]];
        dispatch_async(dispatch_get_main_queue(), ^{
            if([_reservationArray count]!=0){
                [self.tableView reloadData];
                [_HUD hide:YES];
            }else{
                _HUD.labelText = @"暂无历史取号信息";
                [self.tableView reloadData];
                [_HUD hide:YES afterDelay:1];
            }
        });
    });
}

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

//设置行高
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 100;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if ([_reservationArray count]==0) {
        self.tableView.separatorStyle = NO;
    }else{
        self.tableView.separatorStyle = YES;
    }
    return [_reservationArray count];
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor whiteColor];
}

//选中一条纪录触发的事件
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.tableView.allowsSelectionDuringEditing == YES) {
        [self updateDeleteButtonTitle];
    }else{
        [tableView deselectRowAtIndexPath:indexPath animated:YES];
        NSInteger row = [indexPath row];
        Reservation *n = [_reservationArray objectAtIndex:row];
        if (n&&![n.status isEqualToString:@"canceled"])
        {
            [self pushHistoryDetail:n andNavController:self.navigationController];
        }
    }
}

- (void)tableView:(UITableView *)tableView didDeselectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Update the delete button's title based on how many items are selected.
    [self updateDeleteButtonTitle];
}

// 是否可编辑
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    return YES;
}

//编辑模式
- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return UITableViewCellEditingStyleDelete;
}

//dataSource
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{  
    static NSString *cellIdentify=@"historyCell";
    HistoryCell *cell=[tableView dequeueReusableCellWithIdentifier:cellIdentify];
    //检查视图中有没闲置的单元格
    if(cell==nil){
        cell=[[HistoryCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentify];
    }
    Reservation *n = _reservationArray[indexPath.row];
    cell.reservationModel=n;

    //[cell setSelectionStyle:UITableViewCellSelectionStyleNone];

    return cell;
}

//弹出历史信息页面
- (void)pushHistoryDetail:(Reservation *)model andNavController:(UINavigationController *)navController
{
    AppraiseViewController *appraise = [[AppraiseViewController alloc] init];
    appraise.merchartID = model.merchantId;
    appraise.accouId = self.accouId;
    appraise.rid=model.id;
    appraise.isCommented=model.isCommented;
    [navController pushViewController:appraise animated:YES];
}

-(void)requestData:(NSString *)urlStr
{
    if ([Helper isConnectionAvailable]){
        NSString *str1= [NSString stringWithFormat:@"%@", urlStr];
        NSString *response =[QuHaoUtil requestDb:str1];
        if([response isEqualToString:@""]){
            //异常处理
            _HUD.labelText = @"网络异常,请稍后再试";
        }else{
            NSArray *jsonObjects=[QuHaoUtil analyseData:response];
            if(jsonObjects==nil){
                //解析错误
                _HUD.labelText = @"网络异常,请稍后再试";
            }else{
                for(int i=0; i < [jsonObjects count];i++ ){
                    Reservation *model=[[Reservation alloc]init];
                    model.name=[[jsonObjects objectAtIndex:i] objectForKey:@"merchantName"];
                    model.merchantId=[[jsonObjects objectAtIndex:i] objectForKey:@"merchantId"];
                    model.id=[[jsonObjects objectAtIndex:i] objectForKey:@"id"];
                    model.isCommented=[[[jsonObjects objectAtIndex:i] objectForKey:@"isCommented"] boolValue];
                    model.imgUrl=[[jsonObjects objectAtIndex:i] objectForKey:@"merchantImage"];
                    model.created = [[jsonObjects objectAtIndex:i] objectForKey:@"created"];
                    model.status = [[jsonObjects objectAtIndex:i] objectForKey:@"status"];

                    [_reservationArray addObject:model];
                }
            }
        }
    }//如果没有网络连接
    else
    {
        _HUD.labelText = @"当前网络不可用";
    }
}

// 更新导航栏按钮
-(void) updateBarButtons
{
    // 如果是允许多选的状态，即进入批量删除模式
    if (self.tableView.allowsSelectionDuringEditing == YES) {
        //更新删除按钮
        [self updateDeleteButtonTitle];
        self.navigationItem.leftBarButtonItems = nil;
        self.navigationItem.leftBarButtonItem = _multiDeleteBarButton;
        self.navigationItem.rightBarButtonItem =_cancelBarButtonItem;
        
        return;
    }
    if (self.tableView.editing == NO) {// 如果是编辑状态，且不属于批量删除状态
        self.navigationItem.leftBarButtonItem = _backButtonItem;
        self.navigationItem.rightBarButtonItem = _editItem;
    }
}

// 更新删除按钮的标题
-(void)updateDeleteButtonTitle
{
    NSArray *selectedRows = [self.tableView indexPathsForSelectedRows];//得到选中行
    BOOL allItemsAreSelected = selectedRows.count == _reservationArray.count;// 是否全选
    BOOL noItemsAreSelected = selectedRows.count == 0;// 选中行数是否为零
    if (allItemsAreSelected || noItemsAreSelected)
    {// 如果是全选或者未选，则删除键为删除全部
        [_mutiButton setTitle:@"全部删除" forState:UIControlStateNormal];
    }
    else
    {// 否则 删除键为删除（选中行数量）
        [_mutiButton setTitle:[NSString stringWithFormat:@"删除 (%ld)", (long)selectedRows.count] forState:UIControlStateNormal];
    }
}

- (void)multiDeleteClicked:(id)sender {
    
    NSArray *selectedRows = [self.tableView indexPathsForSelectedRows];
    BOOL deleteSpecificRows = selectedRows.count > 0;
    Credit * credit = nil;
    [_delArray removeAllObjects];
    // 删除特定的行
    if (deleteSpecificRows)
    {
        // 将所选的行的索引值放在一个集合中进行批量删除
        NSMutableIndexSet *indicesOfItemsToDelete = [NSMutableIndexSet new];
        for (NSIndexPath *selectionIndex in selectedRows)
        {
            credit = (Credit *)_reservationArray[selectionIndex.row];
            [indicesOfItemsToDelete addIndex:selectionIndex.row];
            [_delArray addObject: credit.id];
        }
        if([_delArray count] !=0 ){
            //逻辑删除
            
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                NSString *str1= [NSString stringWithFormat:@"%@%@%@",IP,delHistory,[_delArray componentsJoinedByString:@","]];
                NSString *response =[QuHaoUtil requestDb:str1];
                dispatch_async(dispatch_get_main_queue(), ^{
                    if([response isEqualToString:@""]){
                        //异常处理
                        [Helper showHUD2:@"网络异常,请稍后再试" andView:self.view andSize:100];
                    }else{
                        [_reservationArray removeObjectsAtIndexes:indicesOfItemsToDelete];
                        [self.tableView deleteRowsAtIndexPaths:selectedRows withRowAnimation:UITableViewRowAnimationTop];
                    }
                });
            });
        }
    }
    else
    {
        for (int i=0 ;i< [_reservationArray count]; i++)
        {
            credit = (Credit *)_reservationArray[i];
            [_delArray addObject: credit.id];
        }
        if([_delArray count] !=0 ){
            //逻辑删除
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                NSString *str1= [NSString stringWithFormat:@"%@%@%@",IP,delHistory,[_delArray componentsJoinedByString:@","]];
                NSString *response =[QuHaoUtil requestDb:str1];
                dispatch_async(dispatch_get_main_queue(), ^{
                    if([response isEqualToString:@""]){
                        //异常处理
                        [Helper showHUD2:@"网络异常,请稍后再试" andView:self.view andSize:100];
                    }else{
                        [_reservationArray removeAllObjects];
                        [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationTop];
                    }
                });
            });
        }
    }
    
    [self.tableView setEditing:NO animated:YES];
    self.tableView.allowsMultipleSelectionDuringEditing = NO;
    [self updateBarButtons];

}

// 取消按钮
- (void)cancelButtonClicked:(id)sender {
    
    [self.tableView setEditing:NO animated:YES];
    self.tableView.allowsMultipleSelectionDuringEditing = NO;
    [self updateBarButtons];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

@end
