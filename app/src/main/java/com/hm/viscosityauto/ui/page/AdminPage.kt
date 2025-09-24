package com.hm.viscosityauto.ui.page

import NoPressStateClick
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asi.nav.Nav
import com.hm.viscosityauto.AdminPageRoute
import com.hm.viscosityauto.AvdParamPageRoute
import com.hm.viscosityauto.CleanPageRoute
import com.hm.viscosityauto.DeviceParamPageRoute
import com.hm.viscosityauto.R
import com.hm.viscosityauto.room.admin.AdminRecords
import com.hm.viscosityauto.ui.theme.cardBg
import com.hm.viscosityauto.ui.theme.cardBgBlue
import com.hm.viscosityauto.ui.theme.cardBgGray
import com.hm.viscosityauto.ui.theme.cardBgWhite
import com.hm.viscosityauto.ui.theme.textColorBlue
import com.hm.viscosityauto.ui.page.AdminRole.admin
import com.hm.viscosityauto.ui.theme.dividerColor
import com.hm.viscosityauto.ui.view.BaseButton
import com.hm.viscosityauto.ui.view.BaseDialog
import com.hm.viscosityauto.ui.view.BaseDialogContent
import com.hm.viscosityauto.ui.view.BaseTitle
import com.hm.viscosityauto.utils.ToastUtil
import com.hm.viscosityauto.vm.MainVM
import com.hm.viscosityauto.vm.TestState

object AdminRole {
    const val admin = 1
    const val user = 0
}

@Composable
fun AdminPage(vm: MainVM) {

    val context = LocalContext.current

    val addAdminDialog = remember {
        mutableStateOf(false)
    }
    val delAdminDialog = remember {
        mutableStateOf(false)
    }
    val editAdminDialog = remember {
        mutableStateOf(false)
    }

    var selAdminIndex by remember {
        mutableIntStateOf(-1)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 28.dp)) {
            BaseTitle(title = stringResource(id = R.string.admin_manager), onBack = {
                Nav.back()
            })
        }


        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(start = 70.dp)
        ) {
            Text(
                text = stringResource(id = R.string.cur_admin) + ": ",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = vm.adminInfo.value.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.width(200.dp)
            )

            Spacer(modifier = Modifier.width(32.dp))

            Text(
                text = stringResource(id = R.string.role) + ": ",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(id = if (vm.adminInfo.value.role == admin) R.string.role_admin else R.string.role_user),
                style = MaterialTheme.typography.bodyLarge
            )

        }

        if (vm.adminInfo.value.role == admin) {
            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                text = stringResource(id = R.string.admin_list),
//                style = MaterialTheme.typography.bodySmall
//            )
//            Spacer(modifier = Modifier.height(24.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(cardBg, RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Box(modifier = Modifier.weight(0.5f)) {

                }

                Text(
                    text = stringResource(id = R.string.name),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(id = R.string.pwd),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(id = R.string.role),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }

            LazyColumn(modifier = Modifier.weight(1f), content = {
                itemsIndexed(vm.adminList) { index, bean ->
                    AdminItemView(bean, selAdminIndex == index, onSel = {
                        selAdminIndex = if (it) {
                            index
                        } else {
                            -1
                        }

                    }

//                        onDel = {
//                        delAdminDialog.value = true
//                        selAdmin = bean
//                    }, onEdit = {
//                        selAdmin = bean
//                        editAdminDialog.value = true
//                    }
                    )
                }
            })

            Spacer(modifier = Modifier.height(24.dp))


            //底部菜单
            Row(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                    .background(color = cardBg),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable {
                            addAdminDialog.value = true
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.add_admin),
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
                VerticalDivider(
                    thickness = 1.dp, color = dividerColor, modifier = Modifier.height(50.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable {
                            if (selAdminIndex==-1){
                                ToastUtil.show(context,context.getString(R.string.unselect_user_tip))
                                return@clickable
                            }
                            editAdminDialog.value = true
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.edit),
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
                VerticalDivider(
                    thickness = 1.dp, color = dividerColor, modifier = Modifier.height(50.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable {
                            if (selAdminIndex==-1){
                                ToastUtil.show(context,context.getString(R.string.unselect_user_tip))
                                return@clickable
                            }
                            delAdminDialog.value = true
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.del),
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
                VerticalDivider(
                    thickness = 1.dp, color = dividerColor, modifier = Modifier.height(50.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable {
                            vm.logout()
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.logout),
                        style = MaterialTheme.typography.titleSmall,
                    )
                }

            }
        }
    }


    //添加用户
    if (addAdminDialog.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black.copy(alpha = 0.1f)).NoPressStateClick(onClick = {
                    addAdminDialog.value = false
                }),
            contentAlignment = Alignment.Center
        ) {
            AdminAddView { name, pwd, role ->
                val filters = vm.adminList.filter { it.name == name }
                if (filters.isNotEmpty()) {
                    Toast.makeText(
                        context,
                        context.getText(R.string.user_already_exists),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    vm.addAdmin(AdminRecords(name = name, pwd = pwd, role = role))
                    addAdminDialog.value = false
                }

            }
        }
    }


    //删除用户
    if (delAdminDialog.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            BaseDialogContent(
                stringResource(id = R.string.tip),
                stringResource(id = R.string.confirm_del_admin),
                onConfirm = {
                    delAdminDialog.value = false
                    vm.delAdmin(vm.adminList[selAdminIndex])
                    selAdminIndex = -1
                },
                onCancel = {
                    delAdminDialog.value = false
                })
        }
    }

    //修改用户
    if (editAdminDialog.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black.copy(alpha = 0.1f)).NoPressStateClick(onClick = {
                    editAdminDialog.value = false
                }),
            contentAlignment = Alignment.Center
        ) {
            AdminAddView(
                vm.adminList[selAdminIndex].name,
                vm.adminList[selAdminIndex].pwd,
                vm.adminList[selAdminIndex].role
            ) { name, pwd, role ->
                vm.editAdmin(vm.adminList[selAdminIndex].copy(name = name, pwd = pwd, role = role))
                editAdminDialog.value = false
            }
        }
    }

}

@Composable
fun AdminItemView(
    model: AdminRecords,
    isSel: Boolean = false,
    onSel: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Box(modifier = Modifier.weight(0.5f), contentAlignment = Alignment.Center) {
            Checkbox(
                checked = isSel,
                onCheckedChange = {
                    onSel(it)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = cardBgBlue,
                    uncheckedColor = cardBgBlue
                ),
                modifier = Modifier.size(50.dp)
            )

        }




        Text(
            text = model.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Text(
            text = model.pwd,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f), textAlign = TextAlign.Center

        )

        Text(
            text = stringResource(id = if (model.role == admin) R.string.role_admin else R.string.role_user),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

    }

}


@Composable
fun AdminAddView(
    adminName: String = "",
    adminPwd: String = "",
    adminRole: Int = 1,
    onConfirm: (String, String, Int) -> Unit
) {

    val context = LocalContext.current

    var name by remember {
        mutableStateOf(adminName)
    }

    var pwd by remember {
        mutableStateOf(adminPwd)
    }

    var role by remember {
        mutableIntStateOf(adminRole)
    }

    Box(
        modifier = Modifier
            .size(415.dp, 342.dp)
            .shadow(
                elevation = 16.dp, shape = RoundedCornerShape(10.dp),
            )
            .background(color = cardBgWhite)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(id = if (adminName.isEmpty()) R.string.add_admin else R.string.edit),
                style = MaterialTheme.typography.titleMedium.copy(textColorBlue)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .size(308.dp, 46.dp)
                    .border(
                        width = 1.dp,
                        color = cardBgGray,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .background(color = cardBgWhite)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 14.dp)
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.mipmap.user_icon),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))


                    BasicTextField(
                        value = name,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        enabled = adminName.isEmpty(),
                        singleLine = true,
                        modifier = Modifier
                            .background(color = Color.Transparent)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        onValueChange = {
                            name = it

                        })
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(308.dp, 46.dp)
                    .border(
                        width = 1.dp,
                        color = cardBgGray,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .background(color = cardBgWhite)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 14.dp)
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.mipmap.pwd_icon),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    BasicTextField(
                        value = pwd,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        singleLine = true,
                        modifier = Modifier
                            .background(color = Color.Transparent)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        onValueChange = {
                            pwd = it
                        })
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .width(308.dp)
                    .height(32.dp)
            ) {

                Row(
                    modifier = Modifier.clickable {
                        role = 1
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = role == 1,
                        null,
                        colors = CheckboxDefaults.colors(checkedColor = cardBgBlue)
                    )
                    Text(text = "  " + stringResource(id = R.string.role_admin))
                }
                Spacer(modifier = Modifier.width(32.dp))
                Row(
                    modifier = Modifier.clickable {
                        role = 0
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = role == 0,
                        null,
                        colors = CheckboxDefaults.colors(checkedColor = cardBgBlue)
                    )
                    Text(text = "  " + stringResource(id = R.string.role_user))
                }

            }


            Spacer(modifier = Modifier.height(24.dp))

            BaseButton(
                stringResource(id = R.string.ok),
                modifier = Modifier
                    .size(308.dp, 50.dp),
                style = MaterialTheme.typography.titleSmall.copy(color = Color.White)
            ) {

                if (name.isEmpty()) {
                    Toast.makeText(
                        context,
                        context.getText(R.string.input_name),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@BaseButton
                }


                if (pwd.isEmpty()) {
                    Toast.makeText(
                        context,
                        context.getText(R.string.input_pwd),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@BaseButton
                }

                onConfirm(name, pwd, role)

            }

            Spacer(modifier = Modifier.height(24.dp))

        }

    }
}
