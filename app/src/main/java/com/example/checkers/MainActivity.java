package com.example.checkers;

import android.app.Activity;
//import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.graphics.Color;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;


public class MainActivity extends Activity { //implements OnClickListener, OnTouchListener{
    int sleeptime=1000;
    int sleeprepeats=30;
    String url="http://dmarmon.com/fueltrak/checkers.php?q=";
    protected TextView status;
    protected Button btnRefresh;
    public ImageView[] board = new ImageView[64];
    //    public View viewcell=board[0];
    public Integer[] bdvals = new Integer [64];
    // 0=empty 1==redman 2=whtman 3=redking 4=whtking  - add 5 for yellow boarder

    int i=0;
    int nbrplayers=2;
    int player=1;  //player -  1=red 2=white   whose turn is it
    int step=0;  //steps in one turn 0=ready 1=choose mover 2=choose destination
    int start=0,midpt=0;
    String uniqueID;
    private Thread loop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        board[0]=findViewById(R.id.board0);
        board[1]=findViewById(R.id.board1);
        board[2]=findViewById(R.id.board2);
        board[3]=findViewById(R.id.board3);
        board[4]=findViewById(R.id.board4);
        board[5]=findViewById(R.id.board5);
        board[6]=findViewById(R.id.board6);
        board[7]=findViewById(R.id.board7);
        board[8]=findViewById(R.id.board8);
        board[9]=findViewById(R.id.board9);
        board[10]=findViewById(R.id.board10);
        board[11]=findViewById(R.id.board11);
        board[12]=findViewById(R.id.board12);
        board[13]=findViewById(R.id.board13);
        board[14]=findViewById(R.id.board14);
        board[15]=findViewById(R.id.board15);
        board[16]=findViewById(R.id.board16);
        board[17]=findViewById(R.id.board17);
        board[18]=findViewById(R.id.board18);
        board[19]=findViewById(R.id.board19);
        board[20]=findViewById(R.id.board20);
        board[21]=findViewById(R.id.board21);
        board[22]=findViewById(R.id.board22);
        board[23]=findViewById(R.id.board23);
        board[24]=findViewById(R.id.board24);
        board[25]=findViewById(R.id.board25);
        board[26]=findViewById(R.id.board26);
        board[27]=findViewById(R.id.board27);
        board[28]=findViewById(R.id.board28);
        board[29]=findViewById(R.id.board29);
        board[30]=findViewById(R.id.board30);
        board[31]=findViewById(R.id.board31);
        board[32]=findViewById(R.id.board32);
        board[33]=findViewById(R.id.board33);
        board[34]=findViewById(R.id.board34);
        board[35]=findViewById(R.id.board35);
        board[36]=findViewById(R.id.board36);
        board[37]=findViewById(R.id.board37);
        board[38]=findViewById(R.id.board38);
        board[39]=findViewById(R.id.board39);
        board[40]=findViewById(R.id.board40);
        board[41]=findViewById(R.id.board41);
        board[42]=findViewById(R.id.board42);
        board[43]=findViewById(R.id.board43);
        board[44]=findViewById(R.id.board44);
        board[45]=findViewById(R.id.board45);
        board[46]=findViewById(R.id.board46);
        board[47]=findViewById(R.id.board47);
        board[48]=findViewById(R.id.board48);
        board[49]=findViewById(R.id.board49);
        board[50]=findViewById(R.id.board50);
        board[51]=findViewById(R.id.board51);
        board[52]=findViewById(R.id.board52);
        board[53]=findViewById(R.id.board53);
        board[54]=findViewById(R.id.board54);
        board[55]=findViewById(R.id.board55);
        board[56]=findViewById(R.id.board56);
        board[57]=findViewById(R.id.board57);
        board[58]=findViewById(R.id.board58);
        board[59]=findViewById(R.id.board59);
        board[60]=findViewById(R.id.board60);
        board[61]=findViewById(R.id.board61);
        board[62]=findViewById(R.id.board62);
        board[63]=findViewById(R.id.board63);
        int i=-1;
        while(i<63){++i;
            board[i].setOnClickListener(v -> turn(v));
        }
        btnRefresh=(Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(view -> refreshBoard());
        uniqueID= UUID.randomUUID().toString().substring(0,8);
        status=findViewById(R.id.status);
        status.setOnLongClickListener(view -> kill());
    }
    // ------------ end on create
    public void refreshBoard(){
        if(btnRefresh.getText()=="ASK") {
            btnRefresh.setText("WAIT");
            status.setText("polling "+player);
            sendBoard(1);  //no chg board but polling
            return;
        }else if(btnRefresh.getText()=="PLAY" ||btnRefresh.getText()=="WAIT") {
            return;  // ignore click
        }else{  //button says START
            for(i=0;i<64;i++){
                if((i+(row(i)))%2==0){  //create a new board
                    board[i].setBackgroundColor(Color.BLACK);
                    if(i<24){board[i].setImageDrawable(getResources().getDrawable(R.drawable._1redman));bdvals[i]=1;}
                    else if(i>39){board[i].setImageDrawable(getResources().getDrawable(R.drawable._2whiteman));bdvals[i]=2;}
                    else{board[i].setImageDrawable(null);bdvals[i]=0;}}
                else{board[i].setBackgroundColor(Color.RED);bdvals[i]=0;}
            }
        if(nbrplayers>1) {
            String bdstring =Arrays.toString(bdvals).replace(" ","");
            Thread zero = new Thread() {
                public void run() {
                    String response = sendRecv(url + "0|" + uniqueID + "|"+bdstring);
                    runOnUiThread(() -> {
                        Log.d("in zero thread", response);
                        if (response.equals("wait")) { //opponent has started but not finished turn
                            if (btnRefresh.getText() == "START") {
                                btnRefresh.setText("WAIT");player=2;
//                                status.setText("other start only "+player);
                                status.setText("polling "+player);
                                sendBoard(1);  //no chg board but polling
                                return;
                            }
                        } else if(response.equals("play")){ //you have started - now take first your turn
                            btnRefresh.setText("PLAY");
                            status.setText("initial turn "+player);
                        }else{  //opponent has started and finished first turn - install board and take turn
                            player=2;
                            overlayBoard(response);
                            btnRefresh.setText("PLAY");
                            status.setText("second turn "+player);
                        }
                        if(btnRefresh.getText() != "WAIT"){
                        step = 0;
                        turn(board[0]);}
                    });
                }
            };
            zero.start();
        }else {  //solitaire
            player = 1;  //1=red  2=white
            step = 0;
            turn(board[0]);
        }
    }
    }
    public void overlayBoard(String bd){  //install new board from server
        if(bd.length()>9){
            bd=bd.replace("[","").replace("]","");
            String[] bdarray=bd.split(",");
            for(int j=0;j<64;j++){  //turn string array into integer array
                bdvals[j]=Integer.parseInt(bdarray[j]);}
        }
        for(i=0;i<64;i++){
            if(bdvals[i]==0){board[i].setImageDrawable(null);}
            if(bdvals[i]==1){board[i].setImageDrawable(getResources().getDrawable((R.drawable._1redman)));}
            if(bdvals[i]==2){board[i].setImageDrawable(getResources().getDrawable((R.drawable._2whiteman)));}
            if(bdvals[i]==3){board[i].setImageDrawable(getResources().getDrawable((R.drawable._3redking)));}
            if(bdvals[i]==4){board[i].setImageDrawable(getResources().getDrawable((R.drawable._4whiteking)));}
            if(bdvals[i]==5){board[i].setImageDrawable(getResources().getDrawable((R.drawable._5yellow)));}
            if(bdvals[i]==6){board[i].setImageDrawable(getResources().getDrawable((R.drawable._6redmanyellow)));}
            if(bdvals[i]==7){board[i].setImageDrawable(getResources().getDrawable((R.drawable._7whitemanyellow)));}
            if(bdvals[i]==8){board[i].setImageDrawable(getResources().getDrawable((R.drawable._8redkingyellow)));}
            if(bdvals[i]==9){board[i].setImageDrawable(getResources().getDrawable((R.drawable._9whitekingyellow)));}
        }
    }
    public void turn(View v){ //steps in one turn 0=ready 1=choose mover 2=choose destination
        int cell= Integer.parseInt(bdnbr(v));
        if(bdvals(cell)<5 && step>0){Log.d("turn - error ", "target not highlighted "+cell +" "+bdvals(cell));return;}
        Log.v("turn begin step--------"," beginning step="+ step +" start "+start+" cell "+cell);
//move or jump
        if (step==2){  //move man to this cell - remove man from previous cell
            Log.d("step=2   ","move this man----from "+start+" to "+ cell);
            int bdvalue=bdvals(start);
            if((row(cell)==0 || row(cell)==7)&& bdvalue<3){bdvalue=bdvalue+2;} //make king
            if(bdvalue==1){board[cell].setImageDrawable(getResources().getDrawable(R.drawable._1redman));}
            if(bdvalue==2){board[cell].setImageDrawable(getResources().getDrawable(R.drawable._2whiteman));}
            if(bdvalue==3){board[cell].setImageDrawable(getResources().getDrawable(R.drawable._3redking));}
            if(bdvalue==4){board[cell].setImageDrawable(getResources().getDrawable(R.drawable._4whiteking));}
            bdvals[cell]=bdvalue;
            board[start].setImageDrawable(null); bdvals[start]=0;  //delete from original cell

            if(Math.abs(row(start)-row(cell))==2){ //jumped an opponent
                int jumped=(start+cell)/2;
                board[jumped].setImageDrawable(null); //delete the opponent
                bdvals[jumped]=0;
            }
            if(Math.abs(row(start)-row(cell))==4 || Math.abs(row(start)-row(cell))==0){ //double jump
                int jumped=((start+midpt)/2);  //midpt = start of second jump
                board[jumped].setImageDrawable(null); //delete the opponent
                bdvals[jumped]=0;
                jumped=((midpt+cell)/2);
                board[jumped].setImageDrawable(null); //delete the opponent
                bdvals[jumped]=0;
            }
            clearhighlights();
            if(nbrplayers>1){
                btnRefresh.setText("WAIT");
                status.setText("not your turn "+player);
                sendBoard(0);
            }else{
                player =player%2+1;
                step=0;
            }
            Log.d("step=2   ","after sendBoard"+start+" to "+ cell);
        }

        if (step==1) {  //click on man to move; highlight possible destinations
            start=cell;
            Log.v("step=1--------", "-----------------------start-"+ start);
            step++;
            if (bdvals[cell] >= 5) {
                clearhighlights();
                for(int poss:movesFrom(v)){
                    if(poss>=0) { //Log.d("possmove  ",bdnbr(v));
                        board[poss].setImageDrawable(getResources().getDrawable(R.drawable._5yellow));
                        bdvals[poss] = 5;
                        Log.d("possmoves yellow","cell"+poss);
                    }
                }
            }
            Log.d("turn","end of step 1");
        }

        if(step==0){  //highlight possible movers
            Log.d("step=0   ","highlight movers--------player "+ player);
            int cntmoves=0;
            for (cell=0;cell<64;cell++){  //loop through board - cell is man
                if(bdvals[cell]>0 && bdvals[cell]%2== player%2) {  //this cell contains a man currently on his turn
//                    Log.d("turn 0 ","can move?  cell="+String.valueOf(cell)+" bdval="+String.valueOf(bdvals[cell])+" player"+ player);
                    if(movesFrom(board[cell])[0]>=0){  //who can move
//                        Log.d("   yes - highlight", " cell="+cell +" bdval="+bdvals[cell]);
                        bdvals[cell]=bdvals[cell]+5;
                        if(bdvals[cell]==6){board[cell].setImageDrawable(getResources().getDrawable(R.drawable._6redmanyellow));}
                        if(bdvals[cell]==7){board[cell].setImageDrawable(getResources().getDrawable(R.drawable._7whitemanyellow));}
                        if(bdvals[cell]==8){board[cell].setImageDrawable(getResources().getDrawable(R.drawable._8redkingyellow));}
                        if(bdvals[cell]==9){board[cell].setImageDrawable(getResources().getDrawable(R.drawable._9whitekingyellow));}
                        cntmoves++;
                    }
                }
            }
            if(cntmoves==0){
                status.setText("GAME LOST");
                btnRefresh.setText("START");
                String bdstring =Arrays.toString(bdvals).replace(" ","");
                Thread zero = new Thread() {
                    public void run() {
                        String response = sendRecv(url + "-1|" + uniqueID + "|"+bdstring);
                        Log.d("turn step0","negative one"+response);
                    }
                };
                zero.start();
            }
            step=1;  //next step actually move
            Log.d("turn","-------------end step=0  "+player);
        }
    }
    public void clearhighlights(){
        for(i=0;i<64;i++){int cell=i;
            if(bdvals[cell]>=5){
//                Log.d("clearhighlights",cell+" "+bdvals[cell]);
                bdvals[cell]=bdvals[cell]%5;
                if(bdvals[cell]==1){board[cell].setImageDrawable(getResources().getDrawable(R.drawable._1redman));}
                if(bdvals[cell]==2){board[cell].setImageDrawable(getResources().getDrawable(R.drawable._2whiteman));}
                if(bdvals[cell]==3){board[cell].setImageDrawable(getResources().getDrawable(R.drawable._3redking));}
                if(bdvals[cell]==4){board[cell].setImageDrawable(getResources().getDrawable(R.drawable._4whiteking));}
                if(bdvals[cell]<1){board[cell].setImageDrawable(null);}
            }
        }
    }

    public int[] movesFrom(View v){  //returns an array of possible destinations
        int dest=0; int dest2=0; int dest3=0; int dest4=0;
        int[] ret= {-1,-1,-1,-1,-1,-1};
        int retptr=0;
        int cell=Integer.parseInt(bdnbr(v));
        int[] sides={7,9};  //right and left
//        int dir=0;
        int dirx=3-(player*2);  //direction of play 1=south (Red) -1=north (White)
        int[] dirarray={dirx,-dirx};
        if(bdvals(cell)<3){dirarray[1]=0;}  //if not king, only one dir

        for(int dir:dirarray) {  if(dir==0){break;}
            for (int side : sides) {
                dest = jump(cell, side, dir);
                if (bdvals(dest) == 0) {  //empty cell, move here possible
                    ret[retptr] = dest;
                    retptr++;
                }
                if (bdvals(dest)>0 && (bdvals(dest)%5)%2 == (1 + player) % 2) {   // occupied by opponent - look for jump
//                    Log.d("look for jump",String.valueOf(dest)+" "+bdvals(dest)+" "+player);
                    dest2 = jump(dest, side, dir);  //movement same side and dir
                    if (bdvals(dest2) == 0) {  //jump to cell not occupied - possible jump
                        midpt = dest2;  //midpoint if double jump
                        ret[retptr] = dest2;
                        retptr++;
                    //look for double jump
                        for(int dir2:dirarray) {if (dir2 == 0) {break;}
                            for (int side2 : sides) {
                                dest3 = jump(dest2, side2, dir2);
                                if (bdvals(dest3)>0 &&bdvals(dest3)%2 == (1 + player) % 2) { // occupied by opponent
                                    dest4 = jump(dest3, side2, dir2);
                                    if (bdvals(dest4) == 0) { //empty spot to land
                                        if (ret[retptr - 1] == midpt) {
                                            ret[retptr - 1] = dest4; //overlay midpt
                                        }
                                        else {
                                            ret[retptr] = dest4;
                                            retptr++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
//        Log.d("  moveFrom dest ",String.valueOf(dest2)+ dest3 + dest4+" cell="+cell+" bdval="+bdvals(cell));
//        Log.d("  moveFrom ret ",String.valueOf(ret[0])+ ret[1] + ret[2]+ret[3]);
        return ret;
    }
    public int jump(int cell, int side, int dir){ //inputs starting cell, side (left or right), dir (north or south)
        int dest=cell+side*dir;
        dest=row(dest)==row(cell)+dir?dest:-1;  //must be in next row
        return dest; //output is cell after move or -1 if move is off the board
    }
    public void sendBoard(int flag){  //wrap communication with server in the polling logic - will sleep and loop
        Log.d("before Thread","the turn finished pushed "+uniqueID);
        loop=new Thread() {
            public void run() {
                int i=flag;  //flag=0 write board and loop for answer  =1 ASK = restart looping without saving board
                while (i++ < sleeprepeats) {
                    String bdstring = Arrays.toString(bdvals).replace(" ",""); //string 64 digits csv no spaces
                    String output= i +"|"+uniqueID+"|"+bdstring;  //only i=1 server will store board
                    String response = sendRecv("http://dmarmon.com/fueltrak/checkers.php?q=\"" + output + "\"");
                    response=response.replace(" ","");
//                    Log.d("inThread2","sent    ="+bdstring);
//                    Log.d("inThread2","response="+response+" loop="+i);
                     if(response.equals("stop")) { //game over
                         runOnUiThread(() -> {  //new input - ready btn
                            btnRefresh.setText("START");
                            status.setText("YOU WON");
                         });
                         i=999;  //stop looping
                         loop.interrupt();  //?
                    }
                    else if(response.length()<5 && !response.equals("zero")){
                        Log.d("inThread","response is too short"+response);}
                    else if (!response.equals(bdstring) && !response.equals("zero")) {  //.replace(" ","")
// polling has returned an updated board - install and take turn
                        response=response.replace("[","").replace("]","");
                        String[] bdarray=response.split(",");
                        for(int j=0;j<64;j++){  //turn string array into integer array
                            bdvals[j]=Integer.parseInt(bdarray[j]);}
                        Log.d("in thread4 ", "input=" + uniqueID + " " + i + " response=" + Arrays.toString(bdvals).replace(" ",""));
                        i=999;  //stop looping
                        runOnUiThread(() -> {  //new input - ready btn
                            overlayBoard("");
                            Log.d("overlayBoard","");
                            status.setText("now your turn "+player);
                            btnRefresh.setText("PLAY");
                            step=0;
                            turn(board[1]);
                        });
                        loop.interrupt();
                    }
                    else{ //no change - wait and try again - while loop
                        try{
                            Thread.sleep(sleeptime);
                            Log.d("after sleep","idx.in.out "+i+response+"|"+output);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }  //end of while loop - timeout
                Log.d("end loop","idx="+i);
                if(i<900) {  //not 999=got new board
                    runOnUiThread(() -> {  //new input - ready btn
                        status.setText("OPPONENT delayed");
//                                btnRefresh.setBackgroundColor(Color.BLUE);
                        btnRefresh.setText("ASK");
                    });
                }
            }
        };
        loop.start();
//        status.setText("WAIT end thread");
//        loop.interrupt();
        Log.d("after thread","end of routine - push again?");

    }
    public String sendRecv(String urlstr){
        String response="";
        Log.d("thread sendrecv",urlstr);
        try {
            URL url = new URL(urlstr);
            HttpURLConnection http=(HttpURLConnection) url.openConnection();
            http.connect();
            InputStream is = http.getInputStream();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            response=rd.readLine();
            Log.d("sendRecv"," response= "+response);
        }
        catch (Exception e)	{
            Log.d("conn thread sendrecv",e.toString());
            Log.d("sendrecv2",urlstr);
        }
        return response;
    }
    public boolean kill() {
        status.setText("GAME ENDED");
        btnRefresh.setText("START");
        String bdstring = Arrays.toString(bdvals).replace(" ", "");
        Thread zero = new Thread() {
            public void run() {
                String response = sendRecv(url + "-1|" + uniqueID + "|" + bdstring);
                Log.d("turn step0", "negative one" + response);
            }

        };
        zero.start();
        player=1;
        return false;
    }
    public String bdnbr(View v){  //given view, returns index in board
        String bdid=v.getResources().getResourceName(v.getId());
        return bdid.substring(29);
    }
    int bdvals(int cell){  //same as bdvals[cell] but safe if cell is out of bounds
        if(cell>=0 && cell<64){ //Log.d("bdvals",String.valueOf(cell)+" "+bdvals[cell]);
            return bdvals[cell];}
        else{return cell;}
    }
    public int row(int cell){
        return cell/8;
    }


    public void onPause(){
        super.onPause();
        SharedPreferences MyPref = getSharedPreferences("chk",0);
        SharedPreferences.Editor ed = MyPref.edit();
        int i=0;Log.d("onPause", i +" "+ bdvals[i]);
        while(i<64){
            ed.putInt("board"+ i, bdvals[i]); i++;
        }
        ed.putInt("player",player);
        ed.apply();
    }
    int invert=0;
    public void onResume(){
        super.onResume();
        SharedPreferences MyPref = getSharedPreferences("chk",0);
        int i=0,temp=0;
        while(i<64){temp=temp+MyPref.getInt("board"+i++, 0);}
        // if zero preferences create new board
        if(temp==0){ i=0; while(i<16){bdvals[i]=++i%64;}}
        else{ i=0; while(i<64){
            bdvals[i]=MyPref.getInt("board"+i++, 0);
//            Log.d("onResume",String.valueOf(bdvals[63]));
        }}
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0,1,0,"Invert "+invert+"->"+(invert+1)%2);
//        menu.add(0,2,0,"Sleep iterations");
//        menu.add(0,3,0,"Sleep interval");
        return true;
    }
    public boolean onPrepareOptionsMenu(final Menu menu){
        menu.findItem(1).setTitle(" NbrPlayers "+nbrplayers+">"+(3-nbrplayers));
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==1){
            nbrplayers=3-nbrplayers;
            btnRefresh.setText("START");
            if(nbrplayers==1){status.setText("solitaire");}
            else status.setText("remote");
        }
        if(item.getItemId()>1){
            Log.d("menu","other "+item.getItemId());
        }
        return true;
    }
}

