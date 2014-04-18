//
//  selectListView.m
//  quHaoIos
//
//  Created by sam on 14-4-1.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "selectListView.h"

@implementation selectListView
- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}

- (id)initWithOption:(NSArray *)aOptions
{
    CGRect rect = CGRectMake(0, 25,kDeviceWidth,kDeviceHeight-89);
    if (self = [super initWithFrame:rect])
    {
        //self.hidden = YES;
        self.backgroundColor = [UIColor clearColor];
        UIView *coverView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, rect.size.width, rect.size.height)];
        coverView.backgroundColor = [UIColor blackColor];
        coverView.alpha = 0.5;
        [self addSubview:coverView];
        
        _kDropDownOption = [aOptions copy];
        _kTableView = [[UITableView alloc] initWithFrame:CGRectMake(0,0,rect.size.width, 200)];
        //_kTableView.separatorColor = [UIColor colorWithWhite:1 alpha:.2];
        _kTableView.backgroundColor = [UIColor whiteColor];
        _kTableView.dataSource = self;
        _kTableView.delegate = self;
        [self addSubview:_kTableView];
    }
    return self;
}

#pragma mark - Private Methods
- (void)fadeIn
{
    CGRect frame = _kTableView.frame;
    frame.size.height = 0;
    _kTableView.frame = frame;
    frame.size.height = 200;
    self.hidden = NO;
    [UIView beginAnimations:@"ResizeForView" context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveLinear];
    _kTableView.frame = frame;

    [UIView commitAnimations];
}

- (void)fadeOut
{
    [UIView beginAnimations:@"moveView" context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveLinear];
    [self removeFromSuperview];
    
    [UIView commitAnimations];
}

#pragma mark - Instance Methods
- (void)showInView:(UIView *)aView animated:(BOOL)animated
{
    [aView addSubview:self];
    if (animated) {
        [self fadeIn];
    }
}

#pragma mark - Tableview datasource & delegates
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_kDropDownOption count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentity = @"selectViewCell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentity];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentity];
        cell.textLabel.font = [UIFont fontWithName:@"CourierNewPSMT" size:14.0];
    }
    int row = [indexPath row];
    cell.textLabel.text = [_kDropDownOption objectAtIndex:row] ;
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 40;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    [self.delegate selectListView:self didSelectedIndex:[indexPath row]];
    [self fadeOut];
}

@end
