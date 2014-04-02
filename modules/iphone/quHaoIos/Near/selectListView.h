//
//  selectListView.h
//  quHaoIos
//
//  Created by sam on 14-4-1.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
@interface selectListView : UIView<UITableViewDataSource,UITableViewDelegate>
{
    UITableView *_kTableView;
    NSArray *_kDropDownOption;
}
@property (nonatomic, assign) id delegate;

- (void)fadeOut;
- (void)fadeIn;
- (void)showInView:(UIView *)aView animated:(BOOL)animated;
- (id)initWithOption:(NSArray *)option;

@end

@protocol selectListViewDelegate
- (void)selectListView:(selectListView *)listView didSelectedIndex:(NSInteger)anIndex;
@end
