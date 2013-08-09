package com.mrzon.churchhub;

import java.util.ArrayList;
import java.util.Date;

import com.mrzon.churchhub.model.Church;
import com.mrzon.churchhub.model.Country;
import com.mrzon.churchhub.model.Denomination;
import com.mrzon.churchhub.model.Helper;
import com.mrzon.churchhub.model.Worship;
import com.mrzon.churchhub.model.WorshipWeek;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Region;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CHDialog {
	public static class AddWorshipDialogFragment  extends DialogFragment {
		private Church church;
		private int day=0;
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();

			View v = inflater.inflate(R.layout.dialog_add_worship, null);
			builder.setView(v);
			final EditText cname = (EditText)v.findViewById(R.id.name);
			final EditText stime= (EditText)v.findViewById(R.id.start);
			final EditText etime = (EditText)v.findViewById(R.id.end);
			final Spinner s = (Spinner) v.findViewById(R.id.Spinner01);
			Resources res = getResources();
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
			android.R.layout.simple_spinner_item, res.getStringArray(R.array.days));
			s.setAdapter(adapter);
			s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
			{
			    @Override
			    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			    {
			    	day = position;
			    }

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
				}

			});
			builder.setMessage("Insert information of the worship").
			setPositiveButton(R.string.add_denomination_message_p, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String name = cname.getText().toString();
					String start = stime.getText().toString();
					String end = etime.getText().toString();
					try {
						Worship c = new Worship();
						c.setName(name);
						c.setChurch(church);
						if(start.split(":").length>0) {
							c.setStart(Double.parseDouble(start.split(":")[0])+Double.parseDouble(start.split(":")[1])/60);
						}
						if(end.split(":").length>0) {
							c.setEnd(Double.parseDouble(end.split(":")[0])+Double.parseDouble(end.split(":")[1])/60);
						}
						c.setDay(day);
						//c.setEsDate(new Date(date));
						
						c.submit();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						Toast.makeText(getActivity().getBaseContext(), "Add church failed", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}	                   }
			})
			.setNegativeButton(R.string.add_denomination_message_n, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
			return builder.create();
		}

		public void setChurch(Church church) {
			// TODO Auto-generated method stub
			this.church = church;
		}
	}


	public static class AttendDialogFragment extends DialogFragment {
		private WorshipWeek worship;

		public WorshipWeek getWorship() {
			return worship;
		}

		public void setWorship(WorshipWeek worship) {
			this.worship = worship;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			final EditText input = new EditText(getActivity().getBaseContext());
			builder.setView(input);
			builder.setMessage(R.string.attend_message_dialog)
			.setPositiveButton(R.string.attend_message_p, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Helper.attend(worship,input.getText().toString());
					Toast.makeText(getActivity().getBaseContext(), "Attended", Toast.LENGTH_LONG).show();
				}
			})
			.setNegativeButton(R.string.attend_message_n, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
			// Create the AlertDialog object and return it
			return builder.create();
		}
	}


	public static class LoginDialogFragment extends DialogFragment {
		private Button login;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


			LayoutInflater inflater = getActivity().getLayoutInflater();

			// Inflate and set the layout for the dialog
			// Pass null as the parent view because its going in the dialog layout
			View v = inflater.inflate(R.layout.dialog_login, null);
			builder.setView(v);

			final EditText username = (EditText)v.findViewById(R.id.username);

			final EditText password= (EditText)v.findViewById(R.id.password);
			builder.setMessage(R.string.login_message_dialog)
			.setPositiveButton(R.string.login_message_p, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String name = username.getText().toString();
					String pass = password.getText().toString();
					try {
						ParseUser.logIn(name, pass);
						login.setText("Logout");
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						Toast.makeText(getActivity().getBaseContext(), "Login failed", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}/*
	               		ParseUser.logInInBackground(name,pass, new LogInCallback() {
	            			@Override
	            			public void done(ParseUser arg0, ParseException arg1) {
	            				if(arg1==null) {
	            					login.setText("Logout");
	            					Toast.makeText(getActivity().getBaseContext(), "Login success", Toast.LENGTH_SHORT).show();						
	            				}
	            				else {
	            					Toast.makeText(getActivity().getBaseContext(), "Login failed", Toast.LENGTH_SHORT).show();
	            				}
	            			}
	            		});*/
				}
			})
			.setNegativeButton(R.string.login_message_n, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
			// Create the AlertDialog object and return it
			return builder.create();
		}

		public void setView(Button login) {
			// TODO Auto-generated method stub
			this.login=login;
		}
	}

	public static class BrowseDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.denomination_message_dialog)
			.setItems(R.array.browse_choice, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch(which) {
					case 0:
						Intent intent = new Intent(getActivity(), BrowseDenominationActivity.class);
						startActivity(intent);
						break;
					case 1:
						Intent intent2 = new Intent(getActivity(), BrowseCountryActivity.class);
						startActivity(intent2);
						break;

					}
				}
			});
			return builder.create();
		}
	}

	public static class AddDenominationDialogFragment extends DialogFragment {
		private Button login;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();

			View v = inflater.inflate(R.layout.dialog_add_denomination, null);
			builder.setView(v);
			final EditText dname = (EditText)v.findViewById(R.id.name);
			final EditText abbv= (EditText)v.findViewById(R.id.abbr);
			final EditText web = (EditText)v.findViewById(R.id.website);
			final EditText wiki= (EditText)v.findViewById(R.id.wiki);

			builder.setMessage(R.string.add_denomination_message_dialog)
			.setPositiveButton(R.string.add_denomination_message_p, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String name = dname.getText().toString();
					String abb = abbv.getText().toString();
					String we = web.getText().toString();
					String wik = wiki.getText().toString();

					try {
						Denomination d = new Denomination();
						d.setName(name);
						d.setWikilink(wik);
						d.setWebsite(we);
						d.setAbbreviation(abb);
						d.submit();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						Toast.makeText(getActivity().getBaseContext(), "Add denomination failed", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}	                   }
			})
			.setNegativeButton(R.string.add_denomination_message_n, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
			return builder.create();
		}
	}
	
	public static class AddChurchDialogFragment extends DialogFragment {
		private Button login;
		private Denomination denomination;
		private String address;
		private double lat,lon;
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();

			View v = inflater.inflate(R.layout.dialog_add_church, null);
			builder.setView(v);
			final EditText cname = (EditText)v.findViewById(R.id.churchName);
			final EditText caddress= (EditText)v.findViewById(R.id.churchAddress);
			final EditText cesdate = (EditText)v.findViewById(R.id.esDate);
			final EditText clocation = (EditText)v.findViewById(R.id.location);
			final EditText cwebsite = (EditText)v.findViewById(R.id.website);
			if(address!=null) {
				caddress.setText(address);
			}
			clocation.setText(lat+","+lon);
			
			builder.setMessage("Insert information of the church").
			setPositiveButton(R.string.add_denomination_message_p, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String name = cname.getText().toString();
					String add = caddress.getText().toString();
					String we = cwebsite.getText().toString();
					String loc = clocation.getText().toString();
					String date = cesdate.getText().toString();
					
					try {
						Church c = new Church();
						c.setName(name);
						c.setAddress(add);
						if(loc.split(",").length>0) {
							c.setLat(Double.parseDouble(loc.split(",")[0]));
							c.setLon(Double.parseDouble(loc.split(",")[1]));
						}
						//c.setEsDate(new Date(date));
						c.setWebsite(we);
						c.setDenomination(denomination);
						c.submit();
						if(getActivity() instanceof AddNewChurch) {
							getActivity().finish();
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						Toast.makeText(getActivity().getBaseContext(), "Add church failed", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}	                   }
			})
			.setNegativeButton(R.string.add_denomination_message_n, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
			return builder.create();
		}

		public void setDenomination(Denomination denomination) {
			// TODO Auto-generated method stub
			this.denomination = denomination;
		}

		public void setAddress(String address) {
			// TODO Auto-generated method stub
			this.address = address;
		}
		public void setLatitude(double lat) {
			// TODO Auto-generated method stub
			this.lat = lat;
		}
		public void setLongitude(double lon) {
			// TODO Auto-generated method stub
			this.lon = lon;
		}
	}
	
	public static class AddCountryDialogFragment extends DialogFragment {
		private Button login;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();

			View v = inflater.inflate(R.layout.dialog_add_country, null);
			builder.setView(v);
			final EditText cname = (EditText)v.findViewById(R.id.cname);
			final EditText ciso= (EditText)v.findViewById(R.id.ciso);
			builder.setMessage("Insert information of the country").
			setPositiveButton(R.string.add_denomination_message_p, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String name = cname.getText().toString();
					String iso = ciso.getText().toString();
					
					try {
						Country c = new Country();
						c.setName(name);
						c.setIso(iso);
						c.submit();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						Toast.makeText(getActivity().getBaseContext(), "Add country succeed", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}	                   }
			})
			.setNegativeButton(R.string.add_denomination_message_n, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
			return builder.create();
		}
	}
}
